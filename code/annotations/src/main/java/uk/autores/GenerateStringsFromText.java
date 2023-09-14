package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Context;
import uk.autores.processing.Handler;
import uk.autores.processing.Namer;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Map;
import java.util.Set;

/**
 * For each resource, generates a class with a name derived from the resource name
 * using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameClass(String)}.
 * The class will have a static method called <code>text</code> that returns the resource
 * as a {@link String}.
 */
public final class GenerateStringsFromText implements Handler {

    /**
     * @return visibility; encoding
     * @see ConfigDefs#VISIBILITY
     * @see ConfigDefs#ENCODING
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(ConfigDefs.VISIBILITY, ConfigDefs.ENCODING);
    }

    @Override
    public void handle(Context context) throws Exception {

        String encoding = context.option(ConfigDefs.ENCODING.name()).orElse("UTF-8");
        CharsetDecoder decoder = decoder(encoding);

        for (Map.Entry<String, FileObject> entry : context.resources.entrySet()) {
            String simple = context.namer.simplifyResourceName(entry.getKey());
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
                 JavaWriter writer = new JavaWriter(this, context, escaper, className, entry.getKey())) {

                writeFile(entry, decoder, writer);
            }
        }
    }

    private void writeFile(Map.Entry<String, FileObject> entry,
                           CharsetDecoder decoder,
                           JavaWriter writer) throws IOException {

            writer.nl();
            writer.indent().staticMember("java.lang.String", "text").append("() ").openBrace().nl();

            try (InputStream in = entry.getValue().openInputStream();
                 Reader reader = new InputStreamReader(in, decoder)) {
                writer.append("    return ").string(reader).append(";").nl();
            }

            writer.closeBrace().nl();
    }

    private CharsetDecoder decoder(String encoding) {
        Charset c = Charset.forName(encoding);
        return c.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }
}