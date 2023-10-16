package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.Test;
import uk.autores.cfg.Visibility;
import uk.autores.env.ResourceSets;
import uk.autores.env.TestElement;
import uk.autores.env.TestFileObject;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.*;

import javax.tools.StandardLocation;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class GenerateInputStreamsFromFilesTest {

    private final Handler handler = new GenerateInputStreamsFromFiles();

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(Visibility.DEF));
    }

    @Test
    void handle() throws Exception {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        SortedSet<Resource> files = ResourceSets.largeAndSmallTextFile(env, 1024);

        List<Config> cfg = singletonList(new Config(Visibility.VISIBILITY, Visibility.PUBLIC));
        Map<String, String> generated = generate(env, files, cfg);
        assertEquals(1, generated.size());
    }

    private Map<String, String> generate(TestProcessingEnvironment env, SortedSet<Resource> files, List<Config> cfg) throws Exception {
        Pkg pkg = new Pkg("foo", false);

        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                pkg,
                TestElement.INSTANCE,
                files,
                cfg,
                new Namer()
        );

        handler.handle(context);

        Map<String, String> results = new HashMap<>();

        String className = context.namer().nameClass("foo");

        String qname = pkg.qualifiedClassName(className);
        TestFileObject file = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT).get(qname);
        if (file == null) {
            return results;
        }

        String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

        Reflect.compile(
                qname,
                src
        ).create().get();

        results.put("foo", src);

        return results;
    }
}

