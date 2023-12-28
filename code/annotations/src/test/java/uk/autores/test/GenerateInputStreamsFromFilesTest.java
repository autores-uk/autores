// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.GenerateInputStreamsFromFiles;
import uk.autores.cfg.Name;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Handler;
import uk.autores.handling.Pkg;
import uk.autores.test.testing.HandlerResults;
import uk.autores.test.testing.HandlerTester;

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
        Pkg foo = Pkg.named("foo");
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

