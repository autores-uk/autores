package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.autores.cfg.Visibility;
import uk.autores.env.ResourceSets;
import uk.autores.env.TestElement;
import uk.autores.env.TestFileObject;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.*;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateConstantsFromPropertiesTest {

    private final Handler handler = new GenerateConstantsFromProperties();
    private final String filename = "Consts.properties";
    private TestFileObject file;
    private TestProcessingEnvironment env;

    @BeforeEach
    void init() throws Exception {
        env = new TestProcessingEnvironment();

        String data = "today={0} said \"Today is {1,date}!\"\n";
        data += "\"foo\"=bar\n";

        file = new TestFileObject(true);
        try(OutputStream out = file.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(Visibility.DEF));
    }

    @Test
    void handle() throws Exception {
        testMessages(env, emptyList(), ResourceSets.of(env, filename, file));
    }

    @Test
    void handlePublicVisibility() throws Exception {
        List<Config> config = singletonList(new Config("visibility", "public"));
        testMessages(env, config, ResourceSets.of(env, filename, file));
    }

    @Test
    void reportsBadFilename() throws Exception {
        List<Resource> resources = ResourceSets.junkWithBadFilename(env, "wrongfile.dat");

        testMessages(env, emptyList(), resources);

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    @Test
    void reportsBadNameGeneration() throws Exception {
        List<Resource> resources = ResourceSets.junkWithBadFilename(env, "true.properties");

        testMessages(env, emptyList(), resources);

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    @Test
    void reportsInvalidKeyIdentifier() throws Exception {
        String data = "public=foo";

        file = new TestFileObject(true);
        try(OutputStream out = file.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        testMessages(env, emptyList(), ResourceSets.of(env, filename, file));

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    private void testMessages(TestProcessingEnvironment env,
                              List<Config> config,
                              List<Resource> files) throws Exception {
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                files,
                config,
                new Namer()
        );

        handler.handle(context);

        String qname = "Consts";
        TestFileObject file = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT).get(qname);
        if (file == null) {
            return;
        }

        String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

        Reflect.compile(
                qname,
                src
        ).create().get();
    }
}