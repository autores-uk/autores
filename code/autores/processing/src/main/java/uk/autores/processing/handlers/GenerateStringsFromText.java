package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;
import uk.autores.handling.Resource;
import uk.autores.naming.Namer;

import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Set;

/**
 * <p>{@link Handler} that generates classes that returns file contents as {@link String}s.</p>
 * <p>
 *     Generates a class. Class name is derived from package name
 *     using {@link Namer#nameType(String)} if the name property is unset.
 * </p>
 * <p>
 *     For each resource, generates a method with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameMember(String)}
 *     that returns the resource as a {@link String}.
 * </p>
 * <p>
 *     Resource files over {@link Integer#MAX_VALUE} in size will result in an error during compilation.
 * </p>
 * <p>
 *     The {@link CharsetDecoder} is configured with {@link CodingErrorAction#REPORT}
 *     on malformed input or unmappable characters which will result in build failures.
 * </p>
 */
public class GenerateStringsFromText implements Handler {
    /** Ctor */
    public GenerateStringsFromText() {}

    /**
     * Supported config.
     *
     * @return visibility, encoding, strategy, name
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(CfgVisibility.DEF, CfgEncoding.DEF, CfgStrategy.DEF, CfgName.DEF);
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

        String encoding = context.option(CfgEncoding.DEF).orElse("UTF-8");
        CharsetDecoder decoder = decoder(encoding);
        String strategy = context.option(CfgStrategy.DEF).orElse(CfgStrategy.AUTO);

        var gs = new GenerationState(decoder, className);

        String qualifiedName = context.pkg().qualifiedClassName(className);

        var buf = new ModifiedUtf8Buffer();

        var filer = context.env().getFiler();
        JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
        try (Writer out = javaFile.openWriter();
             var escaper = new UnicodeEscapeWriter(out);
             var writer = new JavaWriter(this, context, escaper, className, "")) {

            for (Resource resource : context.resources()) {
                String name = Naming.member(context, resource);
                Stats stats = stats(resource, name, buf, decoder);

                write(strategy, gs, stats, writer);
            }

            if (gs.needsCopyMethod) {
                writeUtilityCopyMethod(writer);
            }
            if (gs.needsLoadMethod) {
                writeUtilityLoadMethod(writer, gs.decoder.charset().name());
            }
        }
    }

    private static void write(String strategy, GenerationState gs, Stats stats, JavaWriter writer) throws IOException {
        switch (strategy) {
            case CfgStrategy.LAZY:
                writeLazyLoad(gs, stats, writer);
                break;
            case CfgStrategy.INLINE:
            case CfgStrategy.CONST:
                writeInLine(gs, stats, writer);
                break;
            default:
                writeAuto(gs, stats, writer);
        }
    }

    private static void writeAuto(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException {
        if (stats.utf8Size > ModifiedUtf8Buffer.CONST_BYTE_LIMIT) {
            writeLazyLoad(generationState, stats, writer);
        } else {
            writeInLine(generationState, stats, writer);
        }
    }

    private static void writeInLine(GenerationState gs, Stats stats, JavaWriter writer) throws IOException {
        if (stats.utf8Size < ModifiedUtf8Buffer.CONST_BYTE_LIMIT) {
            writeSimpleInline(gs, stats, writer);
            return;
        }

        gs.needsCopyMethod = true;

        int len = (int) stats.utf16Size;
        ModifiedUtf8Buffer buf = gs.buffer;

        writeMethodDeclaration(writer, stats.name);

        try (InputStream in = stats.resource.open();
             var reader = new InputStreamReader(in, gs.decoder);
             var bufReader = new BufferedReader(reader)) {

            writer.indent().append("char[] arr = new char[").append(len).append("];").nl();
            writer.indent().append("int offset = 0;").nl();
            while (buf.receive(bufReader)) {
                writer.indent().append("offset = ")
                        .append(gs.utilityTypeClassName)
                        .append(".copy$(");
                writeLiteral(writer, buf);
                writer.append(", arr, offset);").nl();
            }
            writer.indent().append("return new java.lang.String(arr);").nl();
        }

        writeMethodClose(writer);
    }

    private static void writeSimpleInline(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException {
        writeMethodDeclaration(writer, stats.name);

        try (InputStream in = stats.resource.open();
             var reader = new InputStreamReader(in, generationState.decoder);
             var bufReader = new BufferedReader(reader, generationState.buffer.maxBuffer())) {

            generationState.buffer.receive(bufReader);
            writer.indent().append("return");
            writeLiteral(writer, generationState.buffer);
            writer.append(";").nl();
        }

        writeMethodClose(writer);
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

    private static void writeLazyLoad(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException {
        generationState.needsLoadMethod = true;

        int size = (int) stats.utf16Size;

        writeMethodDeclaration(writer, stats.name);

        writer.indent().append("return ")
                .append("load$(")
                .string(stats.resource)
                .append(", ")
                .append(size)
                .append(");").nl();

        writeMethodClose(writer);
    }

    private static void writeMethodDeclaration(JavaWriter writer, String name) throws IOException {
        writer.nl();
        writer.indent().staticMember("java.lang.String", name).append("() ").openBrace().nl();
    }

    private static void writeMethodClose(JavaWriter writer) throws IOException {
        writer.closeBrace().nl();
    }

    private static void writeUtilityCopyMethod(JavaWriter writer) throws IOException {
        String decl = "static int copy$(java.lang.CharSequence src, char[] dest, int off) ";

        writer.nl();
        writer.indent().append(decl).openBrace().nl();
        writer.indent().append("for (int i = 0, len = src.length(); i < len; i++) ").openBrace().nl();
        writer.indent().append("dest[off++] = src.charAt(i);").nl();
        writer.closeBrace().nl();
        writer.indent().append("return off;").nl();
        writer.closeBrace().nl();
    }

    private static void writeUtilityLoadMethod(JavaWriter writer, String encoding) throws IOException {
        String decl = "private static java.lang.String load$(java.lang.String resource, int size) ";

        writer.indent().append(decl).openBrace().nl();
        writer.indent()
                .append("java.nio.charset.Charset enc = java.nio.charset.Charset.forName(")
                .string(encoding)
                .append(");")
                .nl();
        writer.indent().append("char[] buf = new char[size];").nl();
        writer.indent()
                .append("try (")
                .append("java.io.InputStream in = ")
                .openResource("resource", false)
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
        writer.indent().append("if ((offset != size) || (in.read() >= 0)) ").openBrace().nl();
        writer.indent().append("throw new java.lang.AssertionError(\"Modified after compilation:\"+resource);").nl();
        writer.closeBrace().nl();
        writer.closeBrace().append(" catch (java.io.IOException e) ").openBrace().nl();
        writer.indent().append("throw new java.lang.AssertionError(resource, e);").nl();
        writer.closeBrace().nl();
        writer.indent().append("return new java.lang.String(buf);").nl();

        writer.closeBrace().nl();
    }

    private CharsetDecoder decoder(String encoding) {
        var c = Charset.forName(encoding);
        return c.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    private Stats stats(Resource resource, String name, ModifiedUtf8Buffer buf, CharsetDecoder decoder) throws IOException {
        long utf16Size = 0L;
        long utf8Size = 0L;
        try (InputStream in = resource.open();
             var reader = new InputStreamReader(in, decoder);
             var bufReader = new BufferedReader(reader)) {
            while (buf.receive(bufReader)) {
                utf16Size += buf.length();
                utf8Size += buf.utf8Length();

                if (utf16Size > Integer.MAX_VALUE) {
                    // no point continuing
                    break;
                }
            }
        }

        return new Stats(resource, name, utf16Size, utf8Size);
    }

    private record Stats(Resource resource, String name, long utf16Size, long utf8Size) {
    }

    private static final class GenerationState {
        final CharsetDecoder decoder;
        final String utilityTypeClassName;
        final ModifiedUtf8Buffer buffer = new ModifiedUtf8Buffer();
        boolean needsCopyMethod;
        boolean needsLoadMethod;

        private GenerationState(CharsetDecoder decoder, String utilityTypeClassName) {
            this.decoder = decoder;
            this.utilityTypeClassName = utilityTypeClassName;
        }
    }
}
