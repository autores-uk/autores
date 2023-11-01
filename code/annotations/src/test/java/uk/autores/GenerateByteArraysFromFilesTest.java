package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.processing.Config;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;
import uk.autores.testing.HandlerResults;
import uk.autores.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.autores.cfg.Strategy.STRATEGY;

class GenerateByteArraysFromFilesTest {

    private final Handler handler = new GenerateByteArraysFromFiles();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateByteArraysFromFiles().config();
        assertTrue(supported.contains(Visibility.DEF));
        assertTrue(supported.contains(Strategy.DEF));
    }

    @Test
    void canGenerateByteArraysFromFilesAuto() throws Exception {
        canGenerateByteArraysFromFiles(Strategy.AUTO, true);
    }

    @Test
    void canGenerateByteArraysFromFilesLazy() throws Exception {
        canGenerateByteArraysFromFiles(Strategy.LAZY, true);
    }

    @Test
    void canGenerateByteArraysFromFilesInline() throws Exception {
        canGenerateByteArraysFromFiles(Strategy.INLINE, false);
    }

    @Test
    void canGenerateByteArraysFromFilesEncode() throws Exception {
        canGenerateByteArraysFromFiles(Strategy.ENCODE, true);
    }

    private void canGenerateByteArraysFromFiles(String strat, boolean generatesUtilityType) throws Exception {
        List<Config> cfg = singletonList(new Config(STRATEGY, strat));
        HandlerResults results = tester()
                .withConfig(cfg)
                .withLargeAndSmallTextFiles(1024)
                .test();
        results.assertNoErrorMessagesReported();
        int expectedOutputs = generatesUtilityType ? 3 : 2;
        results.assertAllGeneratedFilesCompile(expectedOutputs);
    }

    @Test
    void handlesZeroSkipping() throws Exception {
        String data = "\0\0\0A\0";
        String filename = "foo.txt";

        HandlerResults hr = tester().withResource(filename, data.getBytes(StandardCharsets.UTF_8)).test();
        hr.assertAllGeneratedFilesCompile(1);

        Map<String, String> generated = hr.generatedSource();
        String src = generated.values().iterator().next();

        assertTrue(src.contains("i += 3;"));
        assertTrue(src.contains("i += 1;"));
    }

    @Test
    void reportsBadFilename() throws Exception {
        HandlerResults hr = tester().withBadFilename("true.txt").test();
        hr.assertErrorMessagesReported();
    }

    @Test
    void reportsFileTooBig() throws Exception {
        tester().withInfinitelyLargeFile()
                .test()
                .assertErrorMessagesReported();
    }
}