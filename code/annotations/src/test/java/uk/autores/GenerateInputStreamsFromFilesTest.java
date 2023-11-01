package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Name;
import uk.autores.cfg.Visibility;
import uk.autores.processing.Config;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;
import uk.autores.processing.Pkg;
import uk.autores.testing.HandlerResults;
import uk.autores.testing.HandlerTester;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateInputStreamsFromFilesTest {

    private final Handler handler = new GenerateInputStreamsFromFiles();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(Visibility.DEF));
        assertTrue(supported.contains(Name.DEF));
    }

    @Test
    void canGenerateSourcesFromFiles() throws Exception {
        Pkg foo = new Pkg("foo");
        HandlerResults hr = tester().withPkg(foo).withLargeAndSmallTextFiles(1).test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
        String src = hr.generatedSource().get("foo.foo");
        assertNotNull(src, hr.generatedSource().toString());
    }

    @Test
    void canGenerateNamedSourcesFromFiles() throws Exception {
        List<Config> cfg = Arrays.asList(
                new Config(Name.NAME, "Foo"),
                new Config(Visibility.VISIBILITY, Visibility.PUBLIC)
        );
        HandlerResults hr = tester().withConfig(cfg).withLargeAndSmallTextFiles(1).test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
        String src = hr.generatedSource().get("Foo");
        assertNotNull(src, hr.generatedSource().toString());
    }

    @Test
    void reportsInvalidClassName() throws Exception {
        HandlerResults hr = tester().withLargeAndSmallTextFiles(1).test();
        hr.assertErrorMessagesReported();
    }
}

