// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.custom.handler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import uk.autores.handling.CfgVisibility;
import uk.autores.handling.*;
import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

/**
 * This {@link Handler} example generates a class from a
 * <a href="https://mustache.github.io/mustache.5.html">Mustache</a> template.
 * It decorates {@link GenerateByteArraysFromFiles} to reuse its byte handling functionality
 * and uses {@link GenerateStringsFromText} to load the template.
 */
@ResourceFiles(value = "ImageTemplate.txt", handler = GenerateStringsFromText.class)
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

        // Reuse another handler to embed the bytes
        Namer delegateNamer = new InternalNamer(context.namer());
        Context delegate = context.rebuild()
                .setNamer(delegateNamer)
                .setConfig(emptyList())
                .build();
        byteArrayGenerator.handle(delegate);

        // Context artifacts
        List<Resource> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();
        Element annotatedElement = context.annotated();
        // Mustache template
        String template = ImageTemplate.text();
        // Init template engine
        Template engine = Mustache.compiler().compile(template);

        for (Resource resource : resources) {
            String simple = namer.simplifyResourceName(resource.toString());
            String className = namer.nameType(simple);
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isIdentifier(className)) {
                context.printError(className + " is not a valid class name");
                continue;
            }

            // Template context
            Object itc = new ImageTemplateContext(pkg.toString(), className, visibility);

            // Generate code
            JavaFileObject jfo = filer.createSourceFile(qualifiedName, annotatedElement);
            try (Writer writer = jfo.openWriter()) {
                engine.execute(itc, writer);
            }
        }
    }

    /**
     * This is passed to the template engine.
     */
    public static class ImageTemplateContext {
        public final String pkg;
        public final String className;
        public final String visibility;

        private ImageTemplateContext(String pkg, String className, String visibility) {
            this.pkg = pkg;
            this.className = className;
            this.visibility = visibility;
        }

    }

    private static final class InternalNamer extends Namer {
        private final Namer delegate;

        private InternalNamer(Namer delegate) {
            this.delegate = delegate;
        }

        @Override
        public String nameType(String src) {
            return "Internal$" + delegate.nameType(src);
        }

        @Override
        public String nameMember(String src) {
            return delegate.nameMember(src);
        }
    }
}
