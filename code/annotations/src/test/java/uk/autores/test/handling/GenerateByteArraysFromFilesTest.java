// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.autores.handling.*;
import uk.autores.test.testing.HandlerResults;
import uk.autores.test.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.autores.handling.CfgStrategy.STRATEGY;

class GenerateByteArraysFromFilesTest {

    private final Handler handler = new GenerateByteArraysFromFiles();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new GenerateByteArraysFromFiles().config();
        assertTrue(supported.contains(CfgVisibility.DEF));
        assertTrue(supported.contains(CfgStrategy.DEF));
    }

    @ParameterizedTest
    @ValueSource(strings = {CfgStrategy.AUTO, CfgStrategy.INLINE, CfgStrategy.CONST, CfgStrategy.LAZY})
    void canGenerateByteArraysFromFiles(String strat) throws Exception {
        List<Config> cfg = singletonList(new Config(STRATEGY, strat));
        HandlerResults results = tester()
                .withConfig(cfg)
                .withLargeAndSmallTextFiles(1024)
                .test();
        results.assertNoErrorMessagesReported();
        int expectedOutputs = CfgStrategy.INLINE.equals(strat) ? 2 : 3;
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