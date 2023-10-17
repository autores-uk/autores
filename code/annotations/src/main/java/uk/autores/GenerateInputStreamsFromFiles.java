package uk.autores;

import uk.autores.cfg.Visibility;
import uk.autores.internal.JavaWriter;
import uk.autores.internal.UnicodeEscapeWriter;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public final class GenerateInputStreamsFromFiles implements Handler {

    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(Visibility.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {
        // TODO: custom names

        if (context.pkg().isUnnamed()) {
            context.printError("Unable to generate name for unnamed package");
            return;
        }
        Namer namer = context.namer();
        String segment = context.pkg().lastSegment();
        String className = namer.nameClass(segment);
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
        String method = namer.nameMethod(simple);

        writer.nl();
        writer.indent().staticMember("java.io.InputStream", method).append("() throws java.io.IOException ").openBrace().nl();
        writer.indent().append("return ").openResource(resource.toString()).append(";").nl();
        writer.closeBrace();
    }
}
