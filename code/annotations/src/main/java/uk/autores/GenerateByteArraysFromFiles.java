package uk.autores;

import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * {@link Handler} that, for each resource, generates a class with a name derived from the resource name
 * using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameClass(String)}.
 * The class will have a static method called <code>bytes</code> that returns the resource
 * as a new byte array.
 * </p>
 * <p>
 *     Resource files over {@link Integer#MAX_VALUE} in size will result in an error during compilation.
 * </p>
 */
public final class GenerateByteArraysFromFiles implements Handler {

    /**
     * <p>
     * The class file format limits the number of bytes in method code to 65535.
     * See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3">u4 code_length</a>
     * in the Code attribute.
     * </p>
     *
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
     * Strategy:
     * <ul>
     *     <li>"auto": "inline" for files up to 1kB; "strict" otherwise</li>
     *     <li>"inline": files become bytecode instructions;
     *     limits are untested but back-of-napkin calculations limit this mechanism to ~500MB files</li>
     *     <li>"lazy": files are loaded using using {@link Class#getResourceAsStream(String)} or
     *     {@link ClassLoader#getResourceAsStream(String)};
     *     an {@link AssertionError} is thrown at runtime if the file does not match compile time heuristics</li>
     * </ul>
     *
     * @return visibility strategy
     * @see ConfigDefs#VISIBILITY
     * @see ConfigDefs#STRATEGY
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(ConfigDefs.VISIBILITY, ConfigDefs.STRATEGY);
    }

    @Override
    public void handle(Context context) throws Exception {
        Map<String, FileObject> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();

        ClassGenerator generator = generatorStrategy(context);

        byte[] buf = new byte[MAX_BYTES_PER_METHOD];

        for (Map.Entry<String, FileObject> entry : resources.entrySet()) {
            String resource = entry.getKey();
            String simple = namer.simplifyResourceName(resource);
            String className = namer.nameClass(simple);
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isJavaIdentifier(className)) {
                String msg = "Cannot transform resource name '" + entry.getKey() + "' to class name";
                context.printError(msg);
                continue;
            }

            FileStats stats = stats(buf, resource, entry.getValue());
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
        String strategy = context.option(ConfigDefs.STRATEGY.name()).orElse("auto");
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

        try (InputStream in = stats.file.openInputStream()) {
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

    private static void writeLazyLoad(JavaWriter writer, byte[] buf, FileStats stats) throws IOException {
        writeSignature(writer);

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

    private static FileStats stats(byte[] buf, String resource, FileObject file) throws IOException {
        long size = 0;
        try (InputStream in = file.openInputStream()) {
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
        return new FileStats(resource, file, size);
    }

    private static final class FileStats {
        private final String resource;
        private final FileObject file;
        private final long size;
        private FileStats(String resource, FileObject file, long size) {
            this.resource = resource;
            this.file = file;
            this.size = size;
        }
    }

    @FunctionalInterface
    private interface ClassGenerator {
        void generate(JavaWriter writer, byte[] buf, FileStats stats) throws IOException;
    }
}
