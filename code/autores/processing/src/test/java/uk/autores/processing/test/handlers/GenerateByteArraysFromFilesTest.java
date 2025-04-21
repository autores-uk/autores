// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.autores.handling.Config;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Handler;
import uk.autores.processing.handlers.CfgName;
import uk.autores.processing.handlers.CfgStrategy;
import uk.autores.processing.handlers.CfgVisibility;
import uk.autores.processing.handlers.GenerateByteArraysFromFiles;
import uk.autores.processing.test.testing.HandlerResults;
import uk.autores.processing.test.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.autores.processing.handlers.CfgStrategy.STRATEGY;

class GenerateByteArraysFromFilesTest {

    private final Handler handler = new GenerateByteArraysFromFiles();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(CfgVisibility.DEF));
        assertTrue(supported.contains(CfgStrategy.DEF));
        assertTrue(supported.contains(CfgName.DEF));
    }

    @ParameterizedTest
    @ValueSource(strings = {CfgStrategy.AUTO, CfgStrategy.INLINE, CfgStrategy.CONST, CfgStrategy.LAZY})
    void canGenerateByteArraysFromFiles(String strat) throws Exception {
        List<Config> cfg = asList(new Config(STRATEGY, strat), new Config(CfgName.NAME, "Foo"));
        HandlerResults results = tester()
                .withConfig(cfg)
                .withLargeAndSmallTextFiles(1024)
                .test();
        results.assertNoErrorMessagesReported();
        results.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void handlesZeroSkipping() throws Exception {
        String data = "\0\0\0A\0";
        String filename = "foo.txt";

        List<Config> cfg = Collections.singletonList(new Config(CfgName.NAME, "Foo"));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, data.getBytes(StandardCharsets.UTF_8))
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);

        Map<String, String> generated = hr.generatedSource();
        String src = generated.values().iterator().next();

        assertTrue(src.contains("i += 3;"));
        assertTrue(src.contains("i += 1;"));
    }

    @Test
    void reportsBadFilename() throws Exception {
        List<Config> cfg = Collections.singletonList(new Config(CfgName.NAME, "Foo"));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withBadFilename("true.txt")
                .test();
        hr.assertErrorMessagesReported();
    }

    @Test
    void reportsFileTooBig() throws Exception {
        List<Config> cfg = Collections.singletonList(new Config(CfgName.NAME, "Foo"));
        tester().withConfig(cfg)
                .withInfinitelyLargeFile()
                .test()
                .assertErrorMessagesReported();
    }
}