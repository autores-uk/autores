package uk.autores.test;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.ConfigDefs;
import uk.autores.GenerateStringsFromText;
import uk.autores.test.env.*;
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

class GenerateStringsFromTextTest {

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateStringsFromText().config();
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
        assertTrue(supported.contains(ConfigDefs.ENCODING));
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
            String data = IntStream.rangeClosed(0, 0xFFFF + 1).mapToObj(i -> "a").collect(Collectors.joining());
            String filename = "big.txt";

            TestFileObject text = new TestFileObject(true);
            try (OutputStream out = text.openOutputStream()) {
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }

            env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

            files.put(filename, text);
        }

        for (String strat : Arrays.asList("auto", "inline", "lazy")) {
            Config config = new Config(ConfigDefs.STRATEGY.name(), strat);

            boolean generated = generate(env, files, singletonList(config));

            assertTrue(generated);
        }
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

        boolean generated = generate(env, file(filename, text), emptyList());

        assertFalse(generated);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportsFileTooBig() throws Exception {
        String filename = "massive.txt";

        TestInfiniteFileObject text = new TestInfiniteFileObject();

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, text);

        boolean generated = generate(env, file(filename, text), emptyList());

        assertFalse(generated);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private SortedMap<String, FileObject> file(String resource, FileObject fo) {
        SortedMap<String, FileObject> map = new TreeMap<>();
        map.put(resource, fo);
        return map;
    }

    private boolean generate(TestProcessingEnvironment env,
                          SortedMap<String, FileObject> resources,
                             List<Config> cfg) throws Exception {

        Handler handler = new GenerateStringsFromText();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                resources,
                cfg,
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