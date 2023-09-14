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

class GenerateMessagesFromPropertiesTest {

    private final Handler handler = new GenerateMessagesFromProperties();
    private final String filename = "Messages.properties";
    private TestFileObject file;
    private final String filename_fr = "Messages_fr.properties";
    private TestFileObject file_fr;
    private TestProcessingEnvironment env;

    @BeforeEach
    void init() throws Exception {
        env = new TestProcessingEnvironment();

        String data = "today={0} said \"Today is {1,date}!\"\n";
        data += "foo=bar\n";

        file = new TestFileObject(true);
        try(OutputStream out = file.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }

        String data_fr = "today=\"{0} a dit : \u00AB Aujourd'hui, c'est {1,date} ! \u00BB\n";
        data_fr += "foo=baz\n";

        file_fr = new TestFileObject(true);
        try(OutputStream out = file_fr.openOutputStream()) {
            out.write(data_fr.getBytes(StandardCharsets.UTF_8));
        }

        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename_fr, file_fr);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(ConfigDefs.VISIBILITY));
        assertTrue(supported.contains(ConfigDefs.LOCALIZE));
        assertTrue(supported.contains(ConfigDefs.MISSING_KEY));
        assertTrue(supported.contains(ConfigDefs.FORMAT));
    }

    @Test
    void handleLocalized() throws Exception {
        testMessages(env, emptyList(), file(filename, file));
    }

    @Test
    void handleUnLocalized() throws Exception {
        env.getFiler().files.get(StandardLocation.CLASS_PATH).remove(filename_fr);

        testMessages(env, emptyList(), file(filename, file));
    }

    @Test
    void handleNoLocalization() throws Exception {
        List<Config> config = singletonList(new Config("localize", "false"));
        testMessages(env, config, file(filename, file));
    }

    @Test
    void handleNoFormat() throws Exception {
        List<Config> config = singletonList(new Config("format", "false"));
        testMessages(env, config, file(filename, file));
    }

    @Test
    void handlePublicVisibility() throws Exception {
        List<Config> config = singletonList(new Config("visibility", "public"));
        testMessages(env, config, file(filename, file));
    }

    @Test
    void handleMissingKeys() throws Exception {
        makeFrFileEmpty();

        List<Config> config = singletonList(new Config("missing-key", "warn"));
        testMessages(env, config, file(filename, file));
    }

    @Test
    void failsOnMissingKeys() throws Exception {
        makeFrFileEmpty();

        testMessages(env, emptyList(), file(filename, file));

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
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
    void reportsInvalidIdentifier() throws Exception {
        env.getFiler().files.get(StandardLocation.CLASS_PATH).remove(filename_fr);

        String data = "public=foo";

        file = new TestFileObject(true);
        try(OutputStream out = file.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);

        testMessages(env, emptyList(), file(filename, file));

        assertFalse(env.getMessager().messages.get(Diagnostic.Kind.ERROR).isEmpty());
    }

    private void makeFrFileEmpty() throws Exception {
        file_fr = new TestFileObject(true);
        try(OutputStream out = file_fr.openOutputStream()) {
            out.write(new byte[0]);
        }

        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename_fr, file_fr);
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

        String qname = "Messages";
        TestFileObject file = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT).get(qname);
        String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

        Reflect.compile(
                qname,
                src
        ).create().get();
    }
}