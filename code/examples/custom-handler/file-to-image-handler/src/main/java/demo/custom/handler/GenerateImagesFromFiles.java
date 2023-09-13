package demo.custom.handler;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import uk.autores.*;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Map;

@ClasspathResource(value = "ImageTemplate.txt", handler = GenerateStringsFromText.class)
public class GenerateImagesFromFiles implements Handler {

    private final Handler decorated = new GenerateByteArraysFromFiles();

    @Override
    public void handle(Context context) throws Exception {
        // Reuse another handler to embed the bytes
        decorated.handle(context);

        // Mustache template
        String template = ImageTemplate.text();
        // Init template engine
        Template engine = Mustache.compiler().compile(template);

        for (Map.Entry<String, FileObject> resource : context.resources.entrySet()) {
            String simple = context.namer.simplifyResourceName(resource.getKey());
            String dataClassName = context.namer.nameClass(simple);
            String className = dataClassName + "Icon";
            String qualifiedName = context.pkg.qualifiedClassName(className);

            if (!Namer.isJavaIdentifier(className)) {
                context.printError(className + " is not a valid class name");
                continue;
            }

            // Template context
            Object ctxt = new ImageTemplateContext(context.pkg.name, className, dataClassName);

            // Generate code
            JavaFileObject jfo = context.env.getFiler().createSourceFile(qualifiedName, context.annotated);
            try (Writer writer = jfo.openWriter()) {
                engine.execute(ctxt, writer);
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
