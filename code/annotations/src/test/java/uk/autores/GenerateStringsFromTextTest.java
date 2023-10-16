package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.cfg.Encoding;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.env.*;
import uk.autores.processing.*;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class GenerateStringsFromTextTest {

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateStringsFromText().config();
        assertTrue(supported.contains(Visibility.DEF));
        assertTrue(supported.contains(Encoding.DEF));
    }

    @Test
    void handle() throws Exception {
        for (String strat : Arrays.asList("auto", "inline", "lazy")) {
            TestProcessingEnvironment env = new TestProcessingEnvironment();

            Config config = new Config(Strategy.STRATEGY, strat);

            SortedSet<Resource> files = ResourceSets.largeAndSmallTextFile(env, 0xFFFF + 1);
            boolean generated = generate(env, files, singletonList(config));

            assertTrue(generated);
        }
    }

    @Test
    void reportsIllegalIdentifier() throws Exception {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedSet<Resource> badFilename = ResourceSets.junkWithBadFilename(env, "void.txt");

        boolean generated = generate(env, badFilename, emptyList());

        assertFalse(generated);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportsFileTooBig() throws Exception {
        String filename = "massive.txt";

        TestInfiniteFileObject file = new TestInfiniteFileObject();

        TestProcessingEnvironment env = new TestProcessingEnvironment();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);

        boolean generated = generate(env, ResourceSets.of(env, filename, file), emptyList());

        assertFalse(generated);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private boolean generate(TestProcessingEnvironment env,
                          SortedSet<Resource> resources,
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

        for (Resource res : resources) {
            String simple = context.namer().simplifyResourceName(res.path());
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