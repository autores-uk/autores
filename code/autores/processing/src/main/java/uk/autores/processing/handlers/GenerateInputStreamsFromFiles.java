// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;
import uk.autores.handling.Resource;
import uk.autores.naming.Namer;

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

    /** Ctor */
    public GenerateInputStreamsFromFiles() {}

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
     * @see CfgVisibility
     * @see CfgName
     */
    @Override
    public Set<ConfigDef> config() {
        return Set.of(CfgVisibility.DEF, CfgName.DEF);
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

        String qualifiedName = context.pkg().qualifiedClassName(className);
        var filer = context.env().getFiler();
        JavaFileObject javaFile = filer.createSourceFile(qualifiedName, context.annotated());
        try (Writer out = javaFile.openWriter();
             var escaper = new UnicodeEscapeWriter(out);
             var writer = new JavaWriter(this, context, escaper, className, "")) {

            for (Resource resource : context.resources()) {
                writeOpenMethod(context, resource, writer);
            }
        }
    }

    private void writeOpenMethod(Context ctxt, Resource resource, JavaWriter writer) throws IOException {
        String method = Naming.member(ctxt, resource);

        writer.nl();
        writer.indent().staticMember("java.io.InputStream", method).append("() throws java.io.IOException ").openBrace().nl();
        writer.indent().append("return ").openResource(resource.toString()).append(";").nl();
        writer.closeBrace().nl();
    }
}
