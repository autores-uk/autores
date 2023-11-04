package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.GenerateStringsFromText;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.autores.cfg.Encoding;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.processing.Config;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;
import uk.autores.test.testing.HandlerResults;
import uk.autores.test.testing.HandlerTester;

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

    @ParameterizedTest
    @ValueSource(strings = {Strategy.AUTO, Strategy.INLINE, Strategy.ENCODE, Strategy.LAZY})
    void canGenerateTextFromFiles(String strategy) throws Exception {
        List<Config> cfg = singletonList(new Config(Strategy.STRATEGY, strategy));
        HandlerResults hr = tester().withLargeAndSmallTextFiles(0xFFFF + 1).withConfig(cfg).test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(3);
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