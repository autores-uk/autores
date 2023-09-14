package uk.autores;

import org.joor.Reflect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestFileObject;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.*;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
    }

    @Test
    void handle() throws Exception {
        testMessages(env, emptyList(), file(filename, file));
    }

    @Test
    void handlePublicVisibility() throws Exception {
        List<Config> config = singletonList(new Config("visibility", "public"));
        testMessages(env, config, file(filename, file));
    }

    @Test
    void reportsBadFilename() throws Exception {
        SortedMap<String, FileObject> resources = new TreeMap<>();
        resources.put(filename, file);
        resources.put("wrongfile.dat", new TestFileObject(true));

        testMessages(env, emptyList(), resources);

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    @Test
    void reportsBadNameGeneration() throws Exception {
        SortedMap<String, FileObject> resources = new TreeMap<>();
        resources.put(filename, file);
        resources.put("true.properties", new TestFileObject(true));

        testMessages(env, emptyList(), resources);

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    @Test
    void reportsInvalidIdentifier() throws Exception {
        String data = "public=foo";

        file = new TestFileObject(true);
        try(OutputStream out = file.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);

        testMessages(env, emptyList(), file(filename, file));

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    private SortedMap<String, FileObject> file(String resource, FileObject fo) {
        SortedMap<String, FileObject> map = new TreeMap<>();
        map.put(resource, fo);
        return map;
    }

    private void testMessages(TestProcessingEnvironment env,
                              List<Config> config,
                              SortedMap<String, FileObject> files) throws Exception {
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
        String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

        Reflect.compile(
                qname,
                src
        ).create().get();
    }
}