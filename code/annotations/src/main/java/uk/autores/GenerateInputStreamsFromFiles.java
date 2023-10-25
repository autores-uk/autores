package uk.autores;

import uk.autores.cfg.Name;
import uk.autores.cfg.Visibility;
import uk.autores.internal.JavaWriter;
import uk.autores.internal.UnicodeEscapeWriter;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * <p>
 *     {@link Handler} that, for each resource, generates a static method with a name derived from the resource name
 *     using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameMember(String)}.
 * </p>
 */
public final class GenerateInputStreamsFromFiles implements Handler {

    /**
     * <p>
     *     All configuration is optional.
     * </p>
     * <p>
     *     Use "name" to set the generated class name.
     *     If absent the last segment of the package name will be used.
     * </p>
     * <p>
     *     Use "visibility" to make the generated class public.
     * </p>
     *
     * @return visibility, name
     * @see Visibility
     * @see Name
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(Visibility.DEF, Name.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {
        Namer namer = context.namer();
        String segment = context.pkg().lastSegment();
        String base = context.option(Name.DEF).orElse(segment);
        String className = namer.nameType(base);

        if (!Namer.isIdentifier(className)) {
            context.printError("Invalid class name: '" + className + "' - set \"name\" configuration option");
            return;
        }

        String qualifiedName = context.pkg().qualifiedClassName(className);

        Filer filer = context.env().getFiler();
        JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
        try (Writer out = javaFile.openWriter();
             Writer escaper = new UnicodeEscapeWriter(out);
             JavaWriter writer = new JavaWriter(this, context, escaper, className, "")) {

            for (Resource resource : context.resources()) {
                writeOpenMethod(namer, resource, writer);
            }
        }
    }

    private void writeOpenMethod(Namer namer, Resource resource, JavaWriter writer) throws IOException {
        String simple = namer.simplifyResourceName(resource.toString());
        String method = namer.nameMember(simple);

        writer.nl();
        writer.indent().staticMember("java.io.InputStream", method).append("() throws java.io.IOException ").openBrace().nl();
        writer.indent().append("return ").openResource(resource.toString()).append(";").nl();
        writer.closeBrace();
    }
}
