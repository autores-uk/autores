package demo.custom.handler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import uk.autores.*;
import uk.autores.processing.Context;
import uk.autores.processing.Handler;
import uk.autores.processing.Namer;
import uk.autores.processing.Pkg;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Map;

@ClasspathResource(value = "ImageTemplate.txt", handler = GenerateStringsFromText.class)
public class GenerateIconsFromFiles implements Handler {

    private final Handler decorated = new GenerateByteArraysFromFiles();

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
