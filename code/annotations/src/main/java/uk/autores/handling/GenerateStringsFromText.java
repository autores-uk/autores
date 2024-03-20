// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>{@link Handler} that generates classes that returns file contents as {@link String}s.</p>
 * <p>
 *     For each resource, generates a class with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameType(String)}.
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
 */
public final class GenerateStringsFromText implements Handler {

    /** Ctor */
    public GenerateStringsFromText() {}

    /**
     * <p>All configuration is optional.</p>
     *
     * <p>
     *     "UTF-8" is assumed if "encoding" is not set and this is the recommended encoding.
     * </p>
     * <p>
     *     Use "visibility" to make the generated classes public.
     * </p>
     *
     * Strategy:
     * <ul>
     *     <li>{@link CfgStrategy#AUTO}: "inline" for files up to 65535B when encoded as modified UTF-8 - the limit for a String constant;
     *     "lazy" otherwise</li>
     *     <li>{@link CfgStrategy#INLINE}: files become {@link String} literals</li>
     *     <li>{@link CfgStrategy#CONST}: alias for "inline"</li>
     *     <li>{@link CfgStrategy#LAZY}: files are loaded using the {@link ClassLoader}</li>
     * </ul>
     *
     * <p>
     *     The lazy strategy requires that the resource file be provided at runtime.
     *     The inline strategy will break down as the resource file approaches 500MB due to class file limitations.
     * </p>
     *
     * @return visibility; encoding; strategy
     * @see CfgVisibility
     * @see CfgEncoding
     * @see CfgStrategy
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(CfgVisibility.DEF, CfgEncoding.DEF, CfgStrategy.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {
        List<Resource> resources = context.resources();
        if (resources.isEmpty()) {
            return;
        }

        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();
        Element annotated = context.annotated();

        String encoding = context.option(CfgEncoding.DEF).orElse("UTF-8");
        CharsetDecoder decoder = decoder(encoding);

        String util = ClassNames.generateClassName(context.resources());
        GenerationState gs = new GenerationState(decoder, util);

        ClassGenerator generator = strategy(context);

        for (Resource res : resources) {
            Stats stats = stats(res, gs.buffer, decoder);
            if (stats.utf16Size > Integer.MAX_VALUE) {
                String msg = "Resource " + res + " too large for String type";
                context.printError(msg);
                continue;
            }

            String simple = namer.simplifyResourceName(res.toString());
            String className = namer.nameType(simple);
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isIdentifier(className)) {
                String msg = "Cannot transform resource name '" + res + "' to class name";
                context.printError(msg);
                continue;
            }

            JavaFileObject javaFile = filer.createSourceFile(qualifiedName, annotated);

            try (Writer out = javaFile.openWriter();
                 Writer escaper = new UnicodeEscapeWriter(out);
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, res)) {

                generator.generate(gs, stats, writer);
            }
        }

        if (gs.needsLoadMethod || gs.needsCopyMethod) {
            writeUtilityType(context, gs);
        }
    }

    private ClassGenerator strategy(Context context) {
        String strategy = context.option(CfgStrategy.DEF).orElse(CfgStrategy.AUTO);
        switch (strategy) {
            case CfgStrategy.LAZY: return GenerateStringsFromText::writeLazyLoad;
            case CfgStrategy.INLINE:
            case CfgStrategy.CONST: return GenerateStringsFromText::writeInLine;
            default: return GenerateStringsFromText::writeAuto;
        }
    }

    private void writeUtilityType(Context context, GenerationState gs) throws IOException {
        Context copy = context.rebuild()
                .setConfig(Collections.emptyList())
                .build();

        Pkg pkg = context.pkg();
        String qualifiedName = pkg.qualifiedClassName(gs.utilityTypeClassName);
        Filer filer = context.env().getFiler();
        JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
        try (Writer out = javaFile.openWriter();
             Writer escaper = new UnicodeEscapeWriter(out);
             JavaWriter writer = new JavaWriter(this, copy, escaper, gs.utilityTypeClassName, "")) {
            if (gs.needsCopyMethod) {
                writeUtilityCopyMethod(writer);
            }
            if (gs.needsLoadMethod) {
                writeUtilityLoadMethod(writer, gs.decoder.charset().name());
            }
        }
    }

    private static void writeUtilityCopyMethod(JavaWriter writer) throws IOException {
        String decl = "static int copy(CharSequence src, char[] dest, int off) ";

        writer.nl();
        writer.indent().append(decl).openBrace().nl();
        writer.indent().append("for (int i = 0, len = src.length(); i < len; i++) ").openBrace().nl();
        writer.indent().append("dest[off++] = src.charAt(i);").nl();
        writer.closeBrace().nl();
        writer.indent().append("return off;").nl();
        writer.closeBrace().nl();
    }

    private static void writeUtilityLoadMethod(JavaWriter writer, String encoding) throws IOException {
        String decl = "static String load(String resource, int size) ";

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
        writer.indent().append("throw new AssertionError(\"Modified after compilation:\"+resource);").nl();
        writer.closeBrace().nl();
        writer.closeBrace().append(" catch (java.io.IOException e) ").openBrace().nl();
        writer.indent().append("throw new AssertionError(resource, e);").nl();
        writer.closeBrace().nl();
        writer.indent().append("return new java.lang.String(buf);").nl();

        writer.closeBrace().nl();
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

        writeMethodDeclaration(writer);

        try (InputStream in = stats.resource.open();
             Reader reader = new InputStreamReader(in, gs.decoder);
             Reader bufReader = new BufferedReader(reader)) {

            writer.indent().append("char[] arr = new char[").append(len).append("];").nl();
            writer.indent().append("int offset = 0;").nl();
            while (buf.receive(bufReader)) {
                writer.indent().append("offset = ")
                        .append(gs.utilityTypeClassName)
                        .append(".copy(").string(buf).append(", arr, offset);").nl();
            }
            writer.indent().append("return new java.lang.String(arr);").nl();
        }

        writeMethodClose(writer);
    }

    private static void writeSimpleInline(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException {
        writeMethodDeclaration(writer);

        try (InputStream in = stats.resource.open();
             Reader reader = new InputStreamReader(in, generationState.decoder);
             Reader bufReader = new BufferedReader(reader, generationState.buffer.maxBuffer())) {

            generationState.buffer.receive(bufReader);
            writer.indent().append("return ").string(generationState.buffer).append(";").nl();
        }

        writeMethodClose(writer);
    }

    private static void writeLazyLoad(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException {
        generationState.needsLoadMethod = true;

        int size = (int) stats.utf16Size;

        writeMethodDeclaration(writer);

        writer.indent().append("return ")
                .append(generationState.utilityTypeClassName)
                .append(".load(")
                .string(stats.resource)
                .append(", ")
                .append(size)
                .append(");").nl();

        writeMethodClose(writer);
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

    private Stats stats(Resource resource, ModifiedUtf8Buffer buf, CharsetDecoder decoder) throws IOException {
        long utf16Size = 0L;
        long utf8Size = 0L;
        try (InputStream in = resource.open();
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

        return new Stats(resource, utf16Size, utf8Size);
    }

    private static final class Stats {
        private final Resource resource;
        private final long utf16Size;
        private final long utf8Size;

        private Stats(Resource resource, long utf16Size, long utf8Size) {
            this.resource = resource;
            this.utf16Size = utf16Size;
            this.utf8Size = utf8Size;
        }
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

    @FunctionalInterface
    private interface ClassGenerator {
        void generate(GenerationState generationState, Stats stats, JavaWriter writer) throws IOException;
    }
}
