package uk.autores;

import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *     {@link Handler} that, for each resource, generates a class with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameType(String)}.
 *     The class will have a static method called <code>bytes</code> that returns the resource
 *     as a new byte array.
 * </p>
 * <p>
 *     Resource files over {@link Integer#MAX_VALUE} in size will result in an error during compilation.
 * </p>
 * <p>
 *     Inline files will be stored as bytecode instructions.
 *     The size of inline files is limited by the class file format to ~500MB.
 * </p>
 * <p>
 *     Lazily loaded files are loaded using {@link Class#getResourceAsStream(String)}.
 *     If the resource file size has changed since compilation an {@link AssertionError} is thrown.
 * </p>
 */
public final class GenerateByteArraysFromFiles implements Handler {

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

    /**
     * <p>All configuration is optional.</p>
     *
     * Strategy:
     * <ul>
     *     <li>"auto": "inline" for files up to 1kB; "lazy" otherwise</li>
     *     <li>"inline": files become bytecode instructions</li>
     *     <li>"lazy": files are loaded using using the {@link ClassLoader}</li>
     * </ul>
     *
     * <p>
     *     Use "visibility" to make the generated classes public.
     * </p>
     *
     * @return visibility strategy
     * @see Visibility
     * @see Strategy
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(Visibility.DEF, Strategy.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {
        List<Resource> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();

        ClassGenerator generator = generatorStrategy(context);

        byte[] buf = new byte[MAX_BYTES_PER_METHOD];

        for (Resource entry : resources) {
            String resource = entry.toString();
            String simple = namer.simplifyResourceName(resource);
            String className = namer.nameType(simple);
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isIdentifier(className)) {
                String msg = "Cannot transform resource name '" + resource + "' to class name";
                context.printError(msg);
                continue;
            }

            FileStats stats = stats(buf, entry);
            if (stats.size > Integer.MAX_VALUE) {
                String err = "Resource " + resource + " too big for byte array; max size is " + Integer.MAX_VALUE;
                context.printError(err);
                continue;
            }

            JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
            try (Writer out = javaFile.openWriter();
                 Writer escaper = new UnicodeEscapeWriter(out);
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, resource)) {
                generator.generate(writer, buf, stats);
            }
        }
    }

    private ClassGenerator generatorStrategy(Context context) {
        String strategy = context.option(Strategy.DEF).orElse(Strategy.AUTO);
        switch (strategy) {
            case "inline": return GenerateByteArraysFromFiles::writeInlineMethods;
            case "lazy": return GenerateByteArraysFromFiles::writeLazyLoad;
            default: return GenerateByteArraysFromFiles::writeAuto;
        }
    }

    private static void writeAuto(JavaWriter writer, byte[] buf, FileStats stats) throws IOException {
        if (stats.size > 1024) {
            writeLazyLoad(writer, buf, stats);
        } else {
            writeInlineMethods(writer, buf, stats);
        }
    }

    private static void writeInlineMethods(JavaWriter writer, byte[] buf, FileStats stats) throws IOException {
        int methodCount = 0;

        try (InputStream in = stats.resource.open()) {
            while(true) {
                int r = in.read(buf);
                if (r < 0) {
                    break;
                }
                writeInlineFillMethod(buf, r, writer, methodCount);
                methodCount++;
            }
        }

        writeInlineBytesMethod(writer, methodCount, (int) stats.size);
    }

    private static void writeInlineFillMethod(byte[] buf, int limit, JavaWriter writer, int index) throws IOException {
        // TODO: encoding scheme that uses string constants/takes transcoding into account

        writer.nl();
        writer.indent()
                .append("private static int fill")
                .append(Ints.toString(index))
                .append("(byte[] b, int i) ")
                .openBrace()
                .nl();
        for (int i = 0; i < limit; i++) {
            byte b = buf[i];
            if (b == 0) {
                // array values already initialized to zero so just increment
                int skip = inlineSkipZeroes(buf, i + 1, limit);
                String skipStr = Ints.toString(skip + 1);
                writer.indent().append("i += ").append(skipStr).append(";").nl();
                i += skip;
            } else {
                String byteStr = Ints.toString(b);
                writer.indent().append("b[i++] = ").append(byteStr).append(";").nl();
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

    private static void writeInlineBytesMethod(JavaWriter writer, int methodCount, int size) throws IOException {
        writeSignature(writer);

        writer.indent().append("byte[] barr = new byte[").append(Ints.toString(size)).append("];").nl();
        writer.indent().append("int idx = 0;").nl();
        for (int i = 0; i < methodCount; i++) {
            String n = Ints.toString(i);
            writer.indent().append("idx = fill").append(n).append("(barr, idx);").nl();
        }

        writeReturn(writer);
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static void writeLazyLoad(JavaWriter writer, byte[] buf, FileStats stats) throws IOException {
        writeSignature(writer);

        // TODO: avoid writing this logic for every resource
        writer.indent().append("byte[] barr = new byte[").append(Ints.toString((int) stats.size)).append("];").nl();
        writer.indent().append("try (java.io.InputStream in = ").openResource(stats.resource).append(") ").openBrace().nl();
        writer.indent().append("int offset = 0;").nl();
        writer.indent().append("while(true) ").openBrace().nl();
        writer.indent().append("int r = in.read(barr, offset, barr.length - offset);").nl();
        writer.indent().append("if (r < 0) { break; }").nl();
        writer.indent().append("offset += r;").nl();
        writer.indent().append("if (offset == barr.length) { break; }").nl();
        writer.closeBrace().nl();
        writer.throwOnModification("(offset != barr.length) || (in.read() >= 0)", stats.resource);
        writer.closeBrace().append(" catch(java.io.IOException e) ").openBrace().nl();
        writer.indent().append("throw new AssertionError(").string(stats.resource).append(", e);").nl();
        writer.closeBrace().nl();

        writeReturn(writer);
    }

    private static void writeSignature(JavaWriter writer) throws IOException {
        writer.nl();
        writer.indent().staticMember("byte[]", "bytes").append("() ").openBrace().nl();
    }

    private static void writeReturn(JavaWriter writer) throws IOException {
        writer.indent().append("return barr;").nl();
        writer.closeBrace().nl();
    }

    private static FileStats stats(byte[] buf, Resource resource) throws IOException {
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
        return new FileStats(resource, size);
    }

    private static final class FileStats {
        private final Resource resource;
        private final long size;
        private FileStats(Resource resource, long size) {
            this.resource = resource;
            this.size = size;
        }
    }

    @FunctionalInterface
    private interface ClassGenerator {
        void generate(JavaWriter writer, byte[] buf, FileStats stats) throws IOException;
    }
}
