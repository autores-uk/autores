package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestFileObject;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Context;
import uk.autores.processing.Handler;
import uk.autores.processing.Namer;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class GenerateStringsFromTextTest {

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateStringsFromText().config();
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
        assertTrue(supported.contains(ConfigDefs.ENCODING));
    }

    @Test
    void handle() throws Exception {
        String data = "abc";
        String filename = "foo.txt";

        TestFileObject text = new TestFileObject(true);
        try(OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        boolean generated = generate(env, file(filename, text));

        assertTrue(generated);
    }

    @Test
    void reportsIllegalIdentifier() throws Exception {
        String data = "abc";
        String filename = "void.txt";

        TestFileObject text = new TestFileObject(true);
        try(OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        boolean generated = generate(env, file(filename, text));

        assertFalse(generated);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private SortedMap<String, FileObject> file(String resource, FileObject fo) {
        SortedMap<String, FileObject> map = new TreeMap<>();
        map.put(resource, fo);
        return map;
    }

    private boolean generate(TestProcessingEnvironment env,
                          SortedMap<String, FileObject> resources) throws Exception {

        Handler handler = new GenerateStringsFromText();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                resources,
                emptyList(),
                new Namer()
        );

        handler.handle(context);

        for (String res : resources.keySet()) {
            String simple = context.namer().simplifyResourceName(res);
            String className = context.namer().nameClass(simple);

            String qname = TestPkgs.P.qualifiedClassName(className);
            TestFileObject file = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT).get(qname);
            if (file == null) {
                return false;
            }
            String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

            Reflect.compile(
                    qname,
                    src
            ).create().get();
        }

        return true;
    }
}