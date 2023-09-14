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
        Map<String, FileObject> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();

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

            JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());

            try (Writer out = javaFile.openWriter();
                 Writer escaper = new UnicodeEscapeWriter(out);
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, resource)) {

                //writeFiles(entry, writer);

                writeInlineMethods(writer, entry.getValue());
            }
        }
    }

    private void writeInlineMethods(JavaWriter writer, FileObject file) throws IOException {
        int methodCount = 0;
        byte[] buf = new byte[MAX_BYTES_PER_METHOD];

        int size = 0;

        try (InputStream in = file.openInputStream()) {
            while(true) {
                int r = in.read(buf);
                if (r < 0) {
                    break;
                }
                writeInlineFillMethod(buf, r, writer, methodCount);

                // TODO: strict math
                size += r;
                methodCount++;
            }
        }

        writeInlineBytesMethod(writer, methodCount, size);
    }

    private void writeInlineFillMethod(byte[] buf, int limit, JavaWriter writer, int index) throws IOException {
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

    private int inlineSkipZeroes(byte[] buf, int offset, int limit) {
        for (int i = offset; i < limit; i++) {
            if (buf[i] != 0) {
                return i - offset;
            }
        }
        return limit - offset;
    }

    private void writeInlineBytesMethod(JavaWriter writer, int methodCount, int size) throws IOException {
        writer.nl();
        writer.indent().staticMember("byte[]", "bytes").append("() ").openBrace().nl();
        writer.indent().append("byte[] barr = new byte[").append(Ints.toString(size)).append("];").nl();
        writer.indent().append("int idx = 0;").nl();
        for (int i = 0; i < methodCount; i++) {
            String n = Ints.toString(i);
            writer.indent().append("idx = fill").append(n).append("(barr, idx);").nl();
        }
        writer.indent().append("return barr;").nl();
        writer.closeBrace().nl();
    }
}
