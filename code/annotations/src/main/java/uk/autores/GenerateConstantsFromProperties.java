package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Context;
import uk.autores.processing.Handler;
import uk.autores.processing.Namer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * The intent of this code generation {@link Handler} is to validate keys used with {@link java.util.ResourceBundle}.
 * For every {@link Properties} file defined as a resource:
 * <ul>
 *    <li>Generates a class based on the .properties file name using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameClass(String)}</li>
 *    <li>Adds a {@link String} constant named from the property key using {@link Namer#nameStaticField(String)}</li>
 * </ul>
 */
public final class GenerateConstantsFromProperties implements Handler {

    private static final String EXTENSION = ".properties";

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

        for (Map.Entry<String, FileObject> entry : context.resources().entrySet()) {
            String resource = entry.getKey();
            if (!resource.endsWith(EXTENSION)) {
                String msg = "Resource names must end in " + EXTENSION + " - got " + resource;
                context.printError(msg);
            } else {
                Properties base = PropLoader.load(entry.getValue());

                writeProperties(context, resource, base);
            }
        }
    }

    private void writeProperties(Context ctxt,
                                 String resource,
                                 Properties base) throws IOException {
        SortedSet<String> keys = new TreeSet<>(base.stringPropertyNames());

        Namer namer = ctxt.namer();
        String simple = namer.simplifyResourceName(resource);
        String name = namer.nameClass(simple);
        if (!Namer.isJavaIdentifier(name)) {
            String msg = "Cannot transform '" + resource + "' into class name.";
            ctxt.printError(msg);
            return;
        }

        String qualified = ctxt.pkg().qualifiedClassName(name);

        Filer filer = ctxt.env().getFiler();
        JavaFileObject jfo = filer.createSourceFile(qualified, ctxt.annotated());
        try (Writer out = jfo.openWriter();
            Writer escaper = new UnicodeEscapeWriter(out);
            JavaWriter writer = new JavaWriter(this, ctxt, escaper, name, resource)) {

            for (String key : keys) {
                writeProperty(ctxt, resource, writer, key);
            }
        }
    }

    private void writeProperty(Context ctxt,
                               String resource,
                               JavaWriter writer,
                               String key) throws IOException {
        String field = ctxt.namer().nameStaticField(key);
        if (!Namer.isJavaIdentifier(field)) {
            String msg = "Cannot transform key '" + key + "' in " + resource + " to field name";
            ctxt.printError(msg);
            return;
        }

        writer.indent().staticFinal("java.lang.String", field).string(key).append(";").nl();
    }
}
