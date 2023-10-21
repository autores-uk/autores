package uk.autores;

import uk.autores.cfg.Visibility;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Use this code generation {@link Handler} to prevent misspelled keys with {@link java.util.ResourceBundle}.
 * For every {@link Properties} file defined as a resource:
 * <ul>
 *    <li>Generates a class based on the .properties file name using {@link Namer#simplifyResourceName(String)} and {@link Namer#nameType(String)}</li>
 *    <li>Adds a {@link String} constant named from the property key using {@link Namer#nameStaticField(String)}</li>
 * </ul>
 */
public final class GenerateConstantsFromProperties implements Handler {

    private static final String EXTENSION = ".properties";

    /**
     * <p>All configuration is optional.</p>
     * <p>
     *     Use "visibility" to make the generated classes public.
     * </p>
     *
     * @return visibility
     * @see Visibility
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(Visibility.DEF);
    }

    @Override
    public void handle(Context context) throws Exception {

        for (Resource res : context.resources()) {
            if (!res.toString().endsWith(EXTENSION)) {
                String msg = "Resource names must end in " + EXTENSION + " - got " + res;
                context.printError(msg);
            } else {
                Properties base = PropLoader.load(res);

                writeProperties(context, res, base);
            }
        }
    }

    private void writeProperties(Context ctxt,
                                 Resource resource,
                                 Properties base) throws IOException {
        SortedSet<String> keys = new TreeSet<>(base.stringPropertyNames());

        Namer namer = ctxt.namer();
        String simple = namer.simplifyResourceName(resource.toString());
        String name = namer.nameType(simple);
        if (!Namer.isIdentifier(name)) {
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
                               Resource resource,
                               JavaWriter writer,
                               String key) throws IOException {
        String field = ctxt.namer().nameStaticField(key);
        if (!Namer.isIdentifier(field)) {
            String msg = "Cannot transform key '" + key + "' in " + resource + " to field name";
            ctxt.printError(msg);
            return;
        }

        writer.indent().staticFinal("java.lang.String", field).string(key).append(";").nl();
    }
}
