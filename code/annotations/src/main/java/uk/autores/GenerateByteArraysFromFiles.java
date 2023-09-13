package uk.autores;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * For each resource, generates a class with a name derived from the resource name
 * using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameClass(String)}.
 * The class will have a static method called <code>bytes</code> that returns the resource
 * as a byte array.
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
     * @return visibility
     * @see ConfigDefs#VISIBILITY
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(ConfigDefs.VISIBILITY);
    }

    @Override
    public void handle(Context context) throws Exception {

        for (Map.Entry<String, FileObject> entry : context.resources.entrySet()) {
            String resource = entry.getKey();
            String simple = context.namer.simplifyResourceName(resource);
            String className = context.namer.nameClass(simple);
            String qualifiedName = context.pkg.qualifiedClassName(className);

            if (!Namer.isJavaIdentifier(className)) {
                String msg = "Cannot transform resource name '" + entry.getKey() + "' to class name";
                context.printError(msg);
                continue;
            }

            JavaFileObject javaFile = context.env.getFiler().createSourceFile(qualifiedName, context.annotated);

            try (Writer out = javaFile.openWriter();
                 Writer escaper = new UnicodeEscapeWriter(out);
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, resource)) {

                //writeFiles(entry, writer);

                writeMethods(writer, entry.getValue());
            }
        }
    }

    private void writeMethods(JavaWriter writer, FileObject file) throws IOException {
        int methodCount = 0;
        byte[] buf = new byte[MAX_BYTES_PER_METHOD];

        int size = 0;

        try (InputStream in = file.openInputStream()) {
            while(true) {
                int r = in.read(buf);
                if (r < 0) {
                    break;
                }
                writeFillMethod(buf, r, writer, methodCount);

                // TODO: strict math
                size += r;
                methodCount++;
            }
        }

        writeBytesMethod(writer, methodCount, size);
    }

    private void writeFillMethod(byte[] buf, int limit, JavaWriter writer, int index) throws IOException {
        writer.nl();
        writer.indent()
                .append("private static int fill")
                .append(String.valueOf(index))
                .append("(byte[] b, int i) ")
                .openBrace()
                .nl();
        for (int i = 0; i < limit; i++) {
            byte b = buf[i];
            if (b == 0) {
                // array values already initialized to zero so just increment
                int skip = skipZeroes(buf, i + 1, limit);
                writer.indent().append("i += ").append(String.valueOf(skip + 1)).append(";").nl();
                i += skip;
            } else {
                writer.indent().append("b[i++] = ").append(String.valueOf(buf[i])).append(";").nl();
            }
        }
        writer.indent().append("return i;").nl();
        writer.closeBrace().nl();
    }

    private int skipZeroes(byte[] buf, int offset, int limit) {
        for (int i = offset; i < limit; i++) {
            if (buf[i] != 0) {
                return i - offset;
            }
        }
        return limit - offset;
    }

    private void writeBytesMethod(JavaWriter writer, int methodCount, int size) throws IOException {
        writer.nl();
        writer.indent().staticMember("byte[]", "bytes").append("() ").openBrace().nl();
        writer.indent().append("byte[] barr = new byte[").append(String.valueOf(size)).append("];").nl();
        writer.indent().append("int idx = 0;").nl();
        for (int i = 0; i < methodCount; i++) {
            writer.indent().append("idx = fill").append(String.valueOf(i)).append("(barr, idx);").nl();
        }
        writer.indent().append("return barr;").nl();
        writer.closeBrace().nl();
    }

//    private void writeFiles(Map.Entry<String, FileObject> entry,
//                            JavaWriter writer) throws IOException {
//
//            writer.nl();
//            writer.indent().staticMember("byte[]", "bytes").append("() ").openBrace().nl();
//            writer.indent().append("byte[] barr = {");
//
//            writeFileAsIntArray(writer, entry.getValue());
//
//            writer.append("};").nl();
//            writer.append("return barr;").nl();
//            writer.closeBrace().nl();
//    }
//
//    private void writeFileAsIntArray(Writer writer, FileObject file) throws IOException {
//        byte[] buf = new byte[64 * 1024];
//
//        try (InputStream in = file.openInputStream()) {
//            int r;
//            while(true) {
//                r = in.read(buf);
//                if (r < 0) {
//                    break;
//                }
//
//                for (int i = 0; i < r; i++) {
//                    writer.append(String.valueOf(buf[i])).append(",");
//                }
//            }
//        }
//    }
}
