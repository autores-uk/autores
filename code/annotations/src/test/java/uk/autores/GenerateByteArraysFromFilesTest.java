package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestFileObject;
import uk.autores.env.TestMassiveFileObject;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.*;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class GenerateByteArraysFromFilesTest {

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateByteArraysFromFiles().config();
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
    }

    @Test
    void handle() throws Exception {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedMap<String, FileObject> files = new TreeMap<>();

        {
            String data = "abc";
            String filename = "foo.txt";

            TestFileObject text = new TestFileObject(true);
            try (OutputStream out = text.openOutputStream()) {
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }

            env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

            files.put(filename, text);
        }
        {
            String data = IntStream.range(0, 1024).mapToObj(i -> "abc").collect(Collectors.joining());
            String filename = "big.txt";

            TestFileObject text = new TestFileObject(true);
            try (OutputStream out = text.openOutputStream()) {
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }

            env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

            files.put(filename, text);
        }

        for (String strat : Arrays.asList("auto", "inline", "lazy")) {
            List<Config> cfg = singletonList(new Config(ConfigDefs.STRATEGY.name(), strat));
            Map<String, String> generated = generate(env, files, cfg);
            assertFalse(generated.isEmpty());
        }
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

        Map<String, String> generated = generate(env, file(filename, text), emptyList());

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

        Map<String, String> generated = generate(env, file(filename, text), emptyList());

        assertTrue(generated.isEmpty());
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportsFileTooBig() throws Exception {
        String filename = "massive.txt";

        TestMassiveFileObject text = new TestMassiveFileObject();

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        Map<String, String> generated = generate(env, file(filename, text), emptyList());

        assertTrue(generated.isEmpty());
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private Map<String, String> generate(TestProcessingEnvironment env, SortedMap<String, FileObject> files, List<Config> cfg) throws Exception {
        Handler handler = new GenerateByteArraysFromFiles();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                files,
                cfg,
                new Namer()
        );

        handler.handle(context);

        Map<String, String> results = new HashMap<>();

        for (String res : files.keySet()) {
            String simple = context.namer().simplifyResourceName(res);
            String className = context.namer().nameClass(simple);

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