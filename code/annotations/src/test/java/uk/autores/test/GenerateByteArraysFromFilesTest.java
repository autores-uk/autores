package uk.autores.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.autores.GenerateByteArraysFromFiles;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Handler;
import uk.autores.test.testing.HandlerResults;
import uk.autores.test.testing.HandlerTester;

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

    @ParameterizedTest
    @ValueSource(strings = {Strategy.AUTO, Strategy.INLINE, Strategy.ENCODE, Strategy.LAZY})
    void canGenerateByteArraysFromFiles(String strat) throws Exception {
        List<Config> cfg = singletonList(new Config(STRATEGY, strat));
        HandlerResults results = tester()
                .withConfig(cfg)
                .withLargeAndSmallTextFiles(1024)
                .test();
        results.assertNoErrorMessagesReported();
        int expectedOutputs = Strategy.INLINE.equals(strat) ? 2 : 3;
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