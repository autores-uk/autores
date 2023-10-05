package uk.autores.custom.handler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import uk.autores.ClasspathResource;
import uk.autores.GenerateByteArraysFromFiles;
import uk.autores.GenerateStringsFromText;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * This trivial example of a {@link Handler} generates a class from a
 * <a href="https://mustache.github.io/mustache.5.html">Mustache</a> template.
 * It decorates {@link GenerateByteArraysFromFiles} to reuse its byte handling functionality
 * and uses {@link GenerateStringsFromText} to load the template.
 */
@ClasspathResource(value = "ImageTemplate.txt", handler = GenerateStringsFromText.class)
public class GenerateIconsFromFiles implements Handler {

    private final Handler decorated = new GenerateByteArraysFromFiles();

    @Override
    public Set<ConfigDef> config() {
        return decorated.config();
    }

    @Override
    public void handle(Context context) throws Exception {
        // Reuse another handler to embed the bytes
        decorated.handle(context);

        // Context artifacts
        Map<String, FileObject> resources = context.resources();
        Namer namer = context.namer();
        Pkg pkg = context.pkg();
        Filer filer = context.env().getFiler();
        Element annotatedElement = context.annotated();
        // Mustache template
        String template = ImageTemplate.text();
        // Init template engine
        Template engine = Mustache.compiler().compile(template);

        for (Map.Entry<String, FileObject> resource : resources.entrySet()) {
            String simple = namer.simplifyResourceName(resource.getKey());
            String dataClassName = namer.nameClass(simple);
            String className = dataClassName + "Icon";
            String qualifiedName = pkg.qualifiedClassName(className);

            if (!Namer.isJavaIdentifier(className)) {
                context.printError(className + " is not a valid class name");
                continue;
            }

            // Template context
            Object itc = new ImageTemplateContext(pkg.name(), className, dataClassName);

            // Generate code
            JavaFileObject jfo = filer.createSourceFile(qualifiedName, annotatedElement);
            try (Writer writer = jfo.openWriter()) {
                engine.execute(itc, writer);
            }
        }
    }

    public static class ImageTemplateContext {
        public final String pkg;
        public final String className;
        public final String dataClassName;

        private ImageTemplateContext(String pkg, String className, String dataClassName) {
            this.pkg = pkg;
            this.className = className;
            this.dataClassName = dataClassName;
        }

    }
}
