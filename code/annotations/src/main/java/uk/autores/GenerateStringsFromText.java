package uk.autores;

import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Map;
import java.util.Set;

/**
 * <p>{@link Handler} that generates classes that returns file contents as {@link String}s.</p>
 * <p>
 *     For each resource, generates a class with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameClass(String)}.
 *     The class will have a static method called <code>text</code> that returns the resource
 *     as a {@link String}.
 * </p>
 * <p>
 *     Resource files over {@link Integer#MAX_VALUE} in size will result in an error during compilation.
 * </p>
 * <p>
 *     The {@link CharsetDecoder} is configured with {@link CodingErrorAction#REPORT}
 *     on malformed input or unmappable characters which will result in build failures.
 * </p>
 * <p>
 *     Inline files will be stored in the class constant pool.
 *     The class file format restricts constants to 65535 bytes.
 *     Strings over this limit will be split across multiple constants and concatenated at runtime.
 *     The size of inline files is limited by the class file format to ~500MB.
 * </p>
 * <p>
 *     Lazily loaded files are loaded using {@link Class#getResourceAsStream(String)} if
 *     {@link ClasspathResource#relative()} is true or {@link ClassLoader#getResourceAsStream(String)} otherwise.
 *     If the resource file size has changed since compilation an {@link AssertionError} is thrown.
 * </p>
 */
public final class GenerateStringsFromText implements Handler {

    /**
     * String literals must fit into the constant pool encoded as UTF-8.
     * See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7">u4 code_length</a>.
     */
    private static final int CONST_BYTE_LIMIT = 65535;

    /**
     * <p>All configuration is optional.</p>
     * Strategy:
     * <ul>
     *     <li>"auto": "inline" for files up to 65535B when encoded as UTF-8 - the limit for a String constant;
     *     "lazy" otherwise</li>
     *     <li>"inline": files become {@link String} literals</li>
     *     <li>"lazy": files are loaded using the {@link ClassLoader}</li>
     * </ul>
     * <p>
     *     "UTF-8" is assumed if "encoding" is not set and this is the recommended encoding.
     * </p>
     *
     * @return visibility; encoding; strategy
     * @see ConfigDefs#VISIBILITY
     * @see ConfigDefs#ENCODING
     * @see ConfigDefs#STRATEGY
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(ConfigDefs.VISIBILITY, ConfigDefs.ENCODING, ConfigDefs.STRATEGY);
    }

    @Override
    public void handle(Context context) throws Exception {
        Map<String, FileObject> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();
        Element annotated = context.annotated();

        String encoding = context.option(ConfigDefs.ENCODING).orElse("UTF-8");
        CharsetDecoder decoder = decoder(encoding);

        Utf8Buffer buf = Utf8Buffer.size(CONST_BYTE_LIMIT);

        Assistants assistants = new Assistants(decoder, buf);

        ClassGenerator generator = strategy(context);

        for (Map.Entry<String, FileObject> entry : resources.entrySet()) {
            String resource = entry.getKey();
            Stats stats = stats(entry, buf, decoder);
            if (stats.utf16Size > Integer.MAX_VALUE) {
                String msg = "Resource " + resource + " to large for String type";
                context.printError(msg);
                continue;
            }

            String simple = namer.simplifyResourceName(resource);
            String className = namer.nameClass(simple);
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isJavaIdentifier(className)) {
                String msg = "Cannot transform resource name '" + entry.getKey() + "' to class name";
                context.printError(msg);
                continue;
            }

            JavaFileObject javaFile = filer.createSourceFile(qualifiedName, annotated);

            try (Writer out = javaFile.openWriter();
                 Writer escaper = new UnicodeEscapeWriter(out);
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, entry.getKey())) {

                generator.generate(assistants, stats, writer);
            }
        }
    }

    private ClassGenerator strategy(Context context) {
        String strategy = context.option(ConfigDefs.STRATEGY).orElse("auto");
        switch (strategy) {
            case "lazy":
                return GenerateStringsFromText::writeLazyLoad;
            case "inline":
                return GenerateStringsFromText::writeInLine;
            default:
                return GenerateStringsFromText::writeAuto;
        }
    }

    private static void writeAuto(Assistants assistants, Stats stats, JavaWriter writer) throws IOException {
        if (stats.utf8Size > CONST_BYTE_LIMIT) {
            writeLazyLoad(assistants, stats, writer);
        } else {
            writeInLine(assistants, stats, writer);
        }
    }

    private static void writeInLine(Assistants assistants, Stats stats, JavaWriter writer) throws IOException {
        if (stats.utf8Size < CONST_BYTE_LIMIT) {
            writeSimpleInline(assistants, stats, writer);
            return;
        }

        String len = Ints.toString((int) stats.utf16Size);
        Utf8Buffer buf = assistants.buffer;

        writeMethodDeclaration(writer);

        try (InputStream in = stats.file.openInputStream();
             Reader reader = new InputStreamReader(in, assistants.decoder);
             Reader bufReader = new BufferedReader(reader)) {

            writer.indent().append("char[] arr = new char[").append(len).append("];").nl();
            writer.indent().append("int offset = 0;").nl();
            while (buf.receive(bufReader)) {
                writer.indent().append("offset = copy(").string(buf).append(", arr, offset);").nl();
            }
            writer.indent().append("return new java.lang.String(arr);").nl();
        }

        writeMethodClose(writer);
        writeCopyMethod(writer);
    }

    private static void writeSimpleInline(Assistants assistants, Stats stats, JavaWriter writer) throws IOException {
        writeMethodDeclaration(writer);

        try (InputStream in = stats.file.openInputStream();
             Reader reader = new InputStreamReader(in, assistants.decoder);
             Reader bufReader = new BufferedReader(reader)) {

            assistants.buffer.receive(bufReader);
            writer.indent().append("return ").string(assistants.buffer).append(";").nl();
        }

        writeMethodClose(writer);
    }

    private static void writeLazyLoad(Assistants assistants, Stats stats, JavaWriter writer) throws IOException {
        String encoding = assistants.decoder.charset().name();
        int size = (int) stats.utf16Size;

        writeMethodDeclaration(writer);

        writer.indent()
                .append("java.nio.charset.Charset enc = java.nio.charset.Charset.forName(")
                .string(encoding)
                .append(");")
                .nl();
        writer.indent().append("char[] buf = new char[").append(Ints.toString(size)).append("];").nl();
        writer.indent()
                .append("try (")
                .append("java.io.InputStream in = ")
                .openResource(stats.resource)
                .append("; java.io.Reader reader = new java.io.InputStreamReader(in, enc)) ")
                .openBrace()
                .nl();

        writer.indent().append("int offset = 0;").nl();
        writer.indent().append("while(true) ").openBrace().nl();
        writer.indent().append("int r = reader.read(buf, offset, buf.length - offset);").nl();
        writer.indent().append("if (r < 0) { break; }").nl();
        writer.indent().append("offset += r;").nl();
        writer.indent().append("if (offset == buf.length) { break; }").nl();
        writer.closeBrace().nl();
        writer.throwOnModification("offset < buf.length || reader.read() >= 0", stats.resource);
        writer.closeBrace().append(" catch (java.io.IOException e) ").openBrace().nl();
        writer.indent().append("throw new AssertionError(").string(stats.resource).append(");").nl();
        writer.closeBrace().nl();
        writer.indent().append("return new java.lang.String(buf);").nl();

        writeMethodClose(writer);
    }

    private static void writeCopyMethod(JavaWriter writer) throws IOException {
        String decl = "private static int copy(java.lang.CharSequence src, char[] dest, int off) ";

        writer.nl();
        writer.indent().append(decl).openBrace().nl();
        writer.indent().append("for (int i = 0, len = src.length(); i < len; i++) ").openBrace().nl();
        writer.indent().append("dest[off++] = src.charAt(i);").nl();
        writer.closeBrace();
        writer.indent().append("return off;").nl();
        writer.close();
    }

    private static void writeMethodDeclaration(JavaWriter writer) throws IOException {
        writer.nl();
        writer.indent().staticMember("java.lang.String", "text").append("() ").openBrace().nl();
    }

    private static void writeMethodClose(JavaWriter writer) throws IOException {
        writer.closeBrace().nl();
    }

    private CharsetDecoder decoder(String encoding) {
        Charset c = Charset.forName(encoding);
        return c.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    private Stats stats(Map.Entry<String, FileObject> entry, Utf8Buffer buf, CharsetDecoder decoder) throws IOException {
        long utf16Size = 0L;
        long utf8Size = 0L;
        try (InputStream in = entry.getValue().openInputStream();
             Reader reader = new InputStreamReader(in, decoder);
             Reader bufReader = new BufferedReader(reader)) {
            while (buf.receive(bufReader)) {
                utf16Size += buf.length();
                utf8Size += buf.utf8Length();

                if (utf16Size > Integer.MAX_VALUE) {
                    // no point continuing
                    break;
                }
            }
        }

        return new Stats(entry.getKey(), entry.getValue(), utf16Size, utf8Size);
    }

    private static final class Stats {
        private final String resource;
        private final FileObject file;
        private final long utf16Size;
        private final long utf8Size;

        private Stats(String resource, FileObject file, long utf16Size, long utf8Size) {
            this.resource = resource;
            this.file = file;
            this.utf16Size = utf16Size;
            this.utf8Size = utf8Size;
        }
    }

    private static final class Assistants {
        private final CharsetDecoder decoder;
        private final Utf8Buffer buffer;

        private Assistants(CharsetDecoder decoder, Utf8Buffer buffer) {
            this.decoder = decoder;
            this.buffer = buffer;
        }
    }

    @FunctionalInterface
    private interface ClassGenerator {
        void generate(Assistants assistants, Stats stats, JavaWriter writer) throws IOException;
    }
}
