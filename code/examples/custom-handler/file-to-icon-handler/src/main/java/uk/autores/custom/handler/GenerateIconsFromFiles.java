// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.custom.handler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import uk.autores.Texts;
import uk.autores.handling.*;
import uk.autores.processing.handlers.*;
import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

/**
 * This {@link Handler} example generates a class from a
 * <a href="https://mustache.github.io/mustache.5.html">Mustache</a> template.
 * It decorates {@link GenerateByteArraysFromFiles} to reuse its byte handling functionality
 * and uses {@link uk.autores.processing.handlers.GenerateStringsFromText} to load the template.
 */
@Texts(value = "ImageTemplate.txt", name = "Resources")
public class GenerateIconsFromFiles implements Handler {

    private final Handler byteArrayGenerator = new GenerateByteArraysFromFiles();

    @Override
    public Set<ConfigDef> config() {
        return byteArrayGenerator.config();
    }

    @Override
    public void handle(Context context) throws Exception {
        // visibility
        String visibility = context.option(CfgVisibility.DEF).orElse("");

        // Class name
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        String className = context.option(CfgName.DEF).orElse("");
        if ("".equals(className)) {
            // derive from package
            String segment = pkg.lastSegment();
            className = namer.nameType(segment);
        }
        if (!Namer.isIdentifier(className)) {
            context.printError("'" + className + "' is not a valid class name");
            return;
        }
        String qualifiedName = pkg.qualifiedClassName(className);

        // Reuse another handler to embed the bytes
        Namer nameDecorator = new InternalNamer(context.namer());
        Context delegate = context.rebuild()
                .setNamer(nameDecorator)
                .setConfig(emptyList())
                .build();
        byteArrayGenerator.handle(delegate);

        // Context artifacts
        List<Resource> resources = context.resources();
        Filer filer = context.env().getFiler();
        Element annotatedElement = context.annotated();

        List<String> methods = new ArrayList<>();
        for (Resource resource : resources) {
            String simple = namer.simplifyResourceName(resource.toString());
            String method = namer.nameMember(simple);
            if (!Namer.isIdentifier(method)) {
                context.printError("'" + className + "' is not a valid method name");
                continue;
            }
            methods.add(method);
        }

        // Mustache template
        String template = Resources.imageTemplate();
        // Init template engine
        Template engine = Mustache.compiler().compile(template);
        // Template context
        Object itc = new ImageTemplateContext(pkg.toString(), className, visibility, methods);

        // Generate code
        JavaFileObject jfo = filer.createSourceFile(qualifiedName, annotatedElement);
        try (Writer writer = jfo.openWriter()) {
            engine.execute(itc, writer);
        }
    }

    /** This is passed to the template engine. */
    public static class ImageTemplateContext {
        public final String pkg;
        public final String className;
        public final String visibility;
        public final List<String> methods;

        public ImageTemplateContext(String pkg, String className, String visibility, List<String> methods) {
            this.pkg = pkg;
            this.className = className;
            this.visibility = visibility;
            this.methods = methods;
        }

        @Override
        public String toString() {
            return "ImageTemplateContext{" +
                    "pkg='" + pkg + '\'' +
                    ", className='" + className + '\'' +
                    ", visibility='" + visibility + '\'' +
                    ", methods=" + methods +
                    '}';
        }
    }

    private static final class InternalNamer extends Namer {
        private final Namer decorated;

        private InternalNamer(Namer decorated) {
            this.decorated = decorated;
        }

        @Override
        public String nameType(String src) {
            return "Internal$" + decorated.nameType(src);
        }

        @Override
        public String nameMember(String src) {
            return decorated.nameMember(src);
        }
    }
}
