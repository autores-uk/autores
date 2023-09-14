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
import java.util.*;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class GenerateByteArraysFromFilesTest {

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateByteArraysFromFiles().config();
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
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

        Map<String, String> generated = generate(env, file(filename, text));

        assertFalse(generated.isEmpty());
    }

    @Test
    void handlesZeroSkipping() throws Exception {
        String data = "\0\0\0A\0";
        String filename = "foo.txt";

        TestFileObject text = new TestFileObject(true);
        try(OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        Map<String, String> generated = generate(env, file(filename, text));

        assertFalse(generated.isEmpty());
        assertTrue(generated.get(filename).contains("i += 3;"));
        assertTrue(generated.get(filename).contains("i += 1;"));
    }

    @Test
    void reportsBadFilename() throws Exception {
        String data = "abc";
        String filename = "true.txt";

        TestFileObject text = new TestFileObject(true);
        try(OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        Map<String, String> generated = generate(env, file(filename, text));

        assertTrue(generated.isEmpty());
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private Map<String, String> generate(TestProcessingEnvironment env, SortedMap<String, FileObject> files) throws Exception {
        Handler handler = new GenerateByteArraysFromFiles();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                files,
                emptyList(),
                new Namer()
        );

        handler.handle(context);

        Map<String, String> results = new HashMap<>();

        for (String res : files.keySet()) {
            String simple = context.namer.simplifyResourceName(res);
            String className = context.namer.nameClass(simple);

            String qname = TestPkgs.P.qualifiedClassName(className);
            TestFileObject file = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT).get(qname);
            if (file == null) {
                continue;
            }

            String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

            Reflect.compile(
                    qname,
                    src
            ).create().get();

            results.put(res, src);
        }

        return results;
    }

    private SortedMap<String, FileObject> file(String resource, FileObject fo) {
        SortedMap<String, FileObject> map = new TreeMap<>();
        map.put(resource, fo);
        return map;
    }
}