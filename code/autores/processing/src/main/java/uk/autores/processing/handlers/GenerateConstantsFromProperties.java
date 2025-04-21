// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;
import uk.autores.handling.Resource;
import uk.autores.naming.Namer;

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
 *    <li>Adds a {@link String} constant named from the property key using {@link Namer#nameConstant(String)}</li>
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
     * @see CfgVisibility
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(CfgVisibility.DEF);
    }

    /** Ctor */
    public GenerateConstantsFromProperties() {}

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
        String className = Naming.type(ctxt, resource);
        if (!Namer.isIdentifier(className)) {
            String msg = "Cannot transform '" + resource + "' into class name.";
            ctxt.printError(msg);
            return;
        }

        String qualified = ctxt.pkg().qualifiedClassName(className);

        Filer filer = ctxt.env().getFiler();
        JavaFileObject jfo = filer.createSourceFile(qualified, ctxt.annotated());
        try (Writer out = jfo.openWriter();
             Writer escaper = new UnicodeEscapeWriter(out);
             JavaWriter writer = new JavaWriter(this, ctxt, escaper, className, resource)) {

            writeBundleName(ctxt, resource, writer);

            SortedSet<String> keys = new TreeSet<>(base.stringPropertyNames());
            for (String key : keys) {
                writeProperty(ctxt, resource, writer, key);
            }
        }
    }

    private void writeProperty(Context ctxt,
                               Resource resource,
                               JavaWriter writer,
                               String key) throws IOException {
        String field = ctxt.namer().nameConstant(key);
        if (!Namer.isIdentifier(field)) {
            String msg = "Cannot transform key '" + key + "' in " + resource + " to field name";
            ctxt.printError(msg);
            return;
        }

        writer.indent().staticFinal("java.lang.String", field).string(key).append(";").nl();
    }

    private void writeBundleName(Context ctxt,
                                 Resource resource,
                                 JavaWriter writer) throws IOException {
        CharSequence bundle = bundleName(ctxt.pkg(), resource);

        writer.nl();
        writer.indent().staticMember("java.lang.String", "bundle").append("() ").openBrace().nl();
        writer.indent().append("return ").string(bundle).append(";").nl();
        writer.closeBrace().nl().nl();
    }

    static CharSequence bundleName(CharSequence pkg, CharSequence resource) {
        int end = resource.length() - EXTENSION.length();
        if (resource.charAt(0) == '/') {
            StringBuilder buf = new StringBuilder(resource.length());
            for (int i = 1; i < end; i++) {
                char ch = resource.charAt(i);
                if (ch == '/') {
                    buf.append('.');
                } else {
                    buf.append(ch);
                }
            }
            return buf;
        }
        StringBuilder buf = new StringBuilder(pkg.length() + resource.length());
        buf.append(pkg).append('.').append(resource.subSequence(0, end));
        return buf;
    }
}
