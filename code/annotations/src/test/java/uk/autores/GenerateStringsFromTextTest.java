package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Encoding;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.processing.Config;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;
import uk.autores.testing.HandlerResults;
import uk.autores.testing.HandlerTester;

import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateStringsFromTextTest {

    private final Handler handler = new GenerateStringsFromText();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateStringsFromText().config();
        assertTrue(supported.contains(Visibility.DEF));
        assertTrue(supported.contains(Encoding.DEF));
    }

    @Test
    void canGenerateTextFromFilesAuto() throws Exception {
        canGenerateTextFromFiles(Strategy.AUTO);
    }

    @Test
    void canGenerateTextFromFilesInline() throws Exception {
        canGenerateTextFromFiles(Strategy.INLINE);
    }

    @Test
    void canGenerateTextFromFilesEncode() throws Exception {
        canGenerateTextFromFiles(Strategy.ENCODE);
    }

    @Test
    void canGenerateTextFromFilesLazy() throws Exception {
        canGenerateTextFromFiles(Strategy.LAZY);
    }

    private void canGenerateTextFromFiles(String strategy) throws Exception {
        List<Config> cfg = singletonList(new Config(Strategy.STRATEGY, strategy));
        HandlerResults hr = tester().withLargeAndSmallTextFiles(0xFFFF + 1).withConfig(cfg).test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(2);
    }

    @Test
    void reportsIllegalIdentifier() throws Exception {
        tester().withBadFilename("void.txt")
                .test()
                .assertErrorMessagesReported();
    }

    @Test
    void reportsFileTooBig() throws Exception {
        tester().withInfinitelyLargeFile()
                .test()
                .assertErrorMessagesReported();
    }
}