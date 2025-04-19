package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;
import uk.autores.handling.Resource;
import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Set;

/**
 * <p>
 *     Generates a class for loading byte data.
 *     For each resource, generates a method with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameMember(String)} .
 *     The class will have a static method called <code>bytes</code> that returns the resource
 *     as a new byte array.
 * </p>
 * <p>
 *     Resource files over {@link Integer#MAX_VALUE} in size will result in an error during compilation.
 * </p>
 */
public class GenerateByteArraysFromFiles implements Handler {

    /**
     * <p>
     * The class file format limits the number of bytes in method code to 65535.
     * See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3">u4 code_length</a>
     * in the Code attribute.
     * </p>
     * <p>
     * Setting a value in the byte array while steering clear of the constant pool might look like this:
     * <pre>
     *        aload_0              # 1 byte
     *        iload_1              # 1 byte
     *        iinc          1, 1   # 3 bytes
     *        bipush        127    # 2 bytes
     *        bastore              # 1 byte
     * </pre>
     * </p>
     */
    private static final int MAX_BYTES_PER_METHOD = 65535 / 8;

    /** Ctor */
    public GenerateByteArraysFromFiles() {}

    private static byte[] inlineBuffer() {
        return new byte[MAX_BYTES_PER_METHOD];
    }

    /**
     * Supported config.
     *
     * @return visibility, strategy, name
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(CfgVisibility.DEF, CfgStrategy.DEF, CfgName.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {
        if (context.resources().isEmpty()) {
            return;
        }

        String className = Naming.type(context);

        if (!Namer.isIdentifier(className)) {
            context.printError("Invalid class name: '" + className + "' - set \"name\" configuration option");
            return;
        }

        String strategy = context.option(CfgStrategy.DEF).orElse(CfgStrategy.AUTO);
        GenerationState gs = new GenerationState(className);

        String qualifiedName = context.pkg().qualifiedClassName(className);

        Filer filer = context.env().getFiler();
        JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
        try (Writer out = javaFile.openWriter();
             Writer escaper = new UnicodeEscapeWriter(out);
             JavaWriter writer = new JavaWriter(this, context, escaper, className, "")) {

            for (Resource resource : context.resources()) {
                String name = Naming.member(context, resource);
                FileStats stats = stats(gs, resource, name);

                write(context, strategy, gs, stats, writer);
            }

            if (gs.needDecodeMethod) {
                writeUtilityDecode(writer);
            }
            if (gs.needsLoadMethod) {
                writeUtilityLoad(writer);
            }
        }
    }

    private static void write(Context ctxt, String strategy, GenerationState gs, FileStats stats, JavaWriter writer) throws IOException {
        if (!Namer.isIdentifier(stats.name)) {
            ctxt.printError("'" + stats.name + "' is not a valid method name.");
            return;
        }
        if (stats.size > Integer.MAX_VALUE) {
            String err = "Resource " + stats.resource + " too big for byte array; max size is " + Integer.MAX_VALUE;
            ctxt.printError(err);
            return;
        }

        switch (strategy) {
            case CfgStrategy.LAZY:
                writeLazyLoad(ctxt, gs, writer, stats);
                return;
            case CfgStrategy.INLINE:
                writeInlineMethods(ctxt, gs, writer, stats);
                return;
            case CfgStrategy.CONST:
                writeStringMethods(ctxt, gs, writer, stats);
                return;
            default:
                writeAuto(ctxt, gs, writer, stats);
        }
    }

    private static void writeUtilityLoad(JavaWriter writer) throws IOException {
        writer.nl();
        writer.indent()
                .staticMember("byte[]", "load$")
                .append("(java.lang.String resource, int size) ")
                .openBrace().nl();

        writer.indent().append("byte[] barr = new byte[size];").nl();
        writer.indent().append("try (java.io.InputStream in = ").openResource("resource", false).append(") ").openBrace().nl();
        writer.indent().append("int offset = 0;").nl();
        writer.indent().append("while(true) ").openBrace().nl();
        writer.indent().append("int r = in.read(barr, offset, barr.length - offset);").nl();
        writer.indent().append("if (r < 0) { break; }").nl();
        writer.indent().append("offset += r;").nl();
        writer.indent().append("if (offset == barr.length) { break; }").nl();
        writer.closeBrace().nl();
        writer.indent().append("if ((offset != size) || (in.read() >= 0)) ").openBrace().nl();
        writer.indent().append("throw new java.lang.AssertionError(\"Modified after compilation:\"+resource);").nl();
        writer.closeBrace().nl();
        writer.closeBrace().append(" catch(java.io.IOException e) ").openBrace().nl();
        writer.indent().append("throw new java.lang.AssertionError(resource, e);").nl();
        writer.closeBrace().nl();
        writer.indent().append("return barr;").nl();

        writer.closeBrace().nl();
    }

    private static void writeUtilityDecode(JavaWriter writer) throws IOException {
        writer.indent().append("static int decode$(java.lang.String s, byte[] barr, int off) ").openBrace().nl();
        writer.indent().append("for (int i = 0, len = s.length(); i < len; i++) ").openBrace().nl();
        writer.indent().append("char c = s.charAt(i);").nl();
        writer.indent().append("barr[off++] = (byte) (c >> 8);").nl();
        writer.indent().append("barr[off++] = (byte) c;").nl();
        writer.closeBrace().nl();
        writer.indent().append("return off;").nl();
        writer.closeBrace().nl();
    }

    private static void writeAuto(Context ctxt, GenerationState gs, JavaWriter writer, FileStats stats) throws IOException {
        if (stats.size <= 128) {
            writeInlineMethods(ctxt, gs, writer, stats);
        } else if (stats.size <= 0xFFFF) {
            writeStringMethods(ctxt, gs, writer, stats);
        } else {
            writeLazyLoad(ctxt, gs, writer, stats);
        }
    }

    private static void writeInlineMethods(Context ctxt, GenerationState gs, JavaWriter writer, FileStats stats) throws IOException {
        byte[] buf = gs.buffer;
        int methodCount = 0;

        try (InputStream in = stats.resource.open()) {
            while(true) {
                int r = in.read(buf);
                if (r < 0) {
                    break;
                }
                writeInlineFillMethod(buf, r, writer, stats.name, methodCount);
                methodCount++;
                checkConstSize(ctxt, stats, methodCount);
            }
        }

        writeInlineBytesMethod(writer, methodCount, stats);
    }

    private static void checkConstSize(Context ctxt, FileStats stats, int count) {
        if (count == 0xFFFF - 10) {
            String msg = stats.resource + " too large - exceeding class constant pool size";
            ctxt.printError(msg);
        }
    }

    private static void writeInlineFillMethod(byte[] buf, int limit, JavaWriter writer, String name, int index) throws IOException {
        writer.nl();
        writer.indent()
                .append("private static int fill$")
                .append(name)
                .append(index)
                .append("(byte[] b, int i) ")
                .openBrace()
                .nl();
        for (int i = 0; i < limit; i++) {
            byte b = buf[i];
            if (b == 0) {
                // array values already initialized to zero so just increment
                int skip = inlineSkipZeroes(buf, i + 1, limit);
                writer.indent().append("i += ").append(skip + 1).append(";").nl();
                i += skip;
            } else {
                writer.indent().append("b[i++] = ").append(b).append(";").nl();
            }
        }
        writer.indent().append("return i;").nl();
        writer.closeBrace().nl();
    }

    private static int inlineSkipZeroes(byte[] buf, int offset, int limit) {
        for (int i = offset; i < limit; i++) {
            if (buf[i] != 0) {
                return i - offset;
            }
        }
        return limit - offset;
    }

    private static void writeInlineBytesMethod(JavaWriter writer, int methodCount, FileStats stats) throws IOException {
        writeSignature(writer, stats.name);

        int size = (int) stats.size;

        writer.indent().append("byte[] barr = new byte[").append(size).append("];").nl();
        writer.indent().append("int idx = 0;").nl();
        for (int i = 0; i < methodCount; i++) {
            writer.indent();
            if (i < methodCount - 1) {
                writer.append("idx = ");
            }
            writer.append("fill$").append(stats.name).append(i).append("(barr, idx);").nl();
        }

        writeReturn(writer);
    }

    private static void writeLazyLoad(Context ctxt, GenerationState gs, JavaWriter writer, FileStats stats) throws IOException {
        checkConstSize(ctxt, stats, 0);

        gs.needsLoadMethod = true;

        writeSignature(writer, stats.name);

        writer.indent().append("byte[] barr = ")
                .append(gs.utilityTypeClassName)
                .append(".load$(")
                .string(stats.resource.toString())
                .append(", ")
                .append((int) stats.size).append(");").nl();

        writeReturn(writer);
    }

    private static void writeStringMethods(Context ctxt, GenerationState gs, JavaWriter writer, FileStats stats) throws IOException {
        gs.needDecodeMethod = true;

        writeSignature(writer, stats.name);

        int size = (int) stats.size;
        writer.indent().append("byte[] barr = new byte[").append(size).append("];").nl();
        writer.indent().append("int off = 0;").nl();

        int constCount = 0;

        ByteHackReader odd;
        try (InputStream in = stats.resource.open();
             ByteHackReader bhr = new ByteHackReader(in);
             BufferedReader br = new BufferedReader(bhr, 0xFFFF)) {
            odd = bhr;

            String util = gs.utilityTypeClassName;
            ModifiedUtf8Buffer buf8 = gs.utf8Buffer();
            while (buf8.receive(br)) {
                writer.indent();
                if (!exhausted(br)) {
                    writer.append("off = ");
                }
                writer.append(util).append(".decode$(");
                writeLiteral(writer, buf8);
                writer.append(", barr, off);").nl();
                constCount++;
                checkConstSize(ctxt, stats, constCount);
            }
        }

        if (odd.lastByteOdd()) {
            int lastIndex = size - 1;
            byte oddByte = odd.getOddByte();
            writer.indent().append("barr[").append(lastIndex).append("] = ").append(oddByte).append(";").nl();
        }

        writeReturn(writer);
    }

    private static boolean exhausted(BufferedReader br) throws IOException {
        br.mark(1);
        int value = br.read();
        br.reset();
        return value < 0;
    }

    private static void writeLiteral(JavaWriter w, CharSequence cs) throws IOException {
        final int len = cs.length();
        final int LIMIT = 12;
        int offset = 0;
        String delim = "";
        while (offset < cs.length()) {
            int c = len - offset;
            c = Math.min(c, LIMIT);
            CharSequence sub = cs.subSequence(offset, offset + c);
            offset += c;
            w.nl().indent().indent().append(delim).string(sub);
            delim = "+ ";
        }
    }

    private static void writeSignature(JavaWriter writer, String name) throws IOException {
        writer.nl();
        writer.indent().staticMember("byte[]", name).append("() ").openBrace().nl();
    }

    private static void writeReturn(JavaWriter writer) throws IOException {
        writer.indent().append("return barr;").nl();
        writer.closeBrace().nl().nl();
    }


    private static FileStats stats(GenerationState gs, Resource resource, String name) throws IOException {
        byte[] buf = gs.buffer;
        long size = 0;
        try (InputStream in = resource.open()) {
            while(true) {
                int r = in.read(buf);
                if (r < 0) {
                    break;
                }
                size += r;
                if (size > Integer.MAX_VALUE) {
                    // no point continuing
                    break;
                }
            }
        }
        return new FileStats(resource, size, name);
    }

    private static final class FileStats {
        private final Resource resource;
        private final long size;
        private final String name;
        private FileStats(Resource resource, long size, String name) {
            this.resource = resource;
            this.size = size;
            this.name = name;
        }
    }

    private static class GenerationState {
        final byte[] buffer = inlineBuffer();
        boolean needsLoadMethod;
        boolean needDecodeMethod;
        ModifiedUtf8Buffer utf8Buffer;
        final String utilityTypeClassName;

        GenerationState(String utilityTypeClassName) {
            this.utilityTypeClassName = utilityTypeClassName;
        }

        ModifiedUtf8Buffer utf8Buffer() {
            if (utf8Buffer == null) {
                utf8Buffer = new ModifiedUtf8Buffer();
            }
            return utf8Buffer;
        }
    }
}
