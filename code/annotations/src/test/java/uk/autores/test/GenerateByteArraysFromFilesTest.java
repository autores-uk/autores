package uk.autores.test;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.ConfigDefs;
import uk.autores.GenerateByteArraysFromFiles;
import uk.autores.processing.*;
import uk.autores.test.env.*;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        SortedSet<Resource> files = ResourceSets.largeAndSmallTextFile(env, 1024);

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

        TestFileObject tfo = new TestFileObject(true);
        try(OutputStream out = tfo.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedSet<Resource> resources = ResourceSets.of(env, filename, tfo);

        Map<String, String> generated = generate(env, resources, emptyList());

        assertFalse(generated.isEmpty());
        assertTrue(generated.get(filename).contains("i += 3;"));
        assertTrue(generated.get(filename).contains("i += 1;"));
    }

    @Test
    void reportsBadFilename() throws Exception {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedSet<Resource> badFile = ResourceSets.junkWithBadFilename(env,"true.txt");

        Map<String, String> generated = generate(env, badFile, emptyList());

        assertTrue(generated.isEmpty());
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportsFileTooBig() throws Exception {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedSet<Resource> infinite = ResourceSets.infinitelyLargeFile(env);
        Map<String, String> generated = generate(env, infinite, emptyList());

        assertTrue(generated.isEmpty());
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private Map<String, String> generate(TestProcessingEnvironment env, SortedSet<Resource> files, List<Config> cfg) throws Exception {
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

        for (Resource res : files) {
            String simple = context.namer().simplifyResourceName(res.path());
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

            results.put(res.path(), src);
        }

        return results;
    }
}