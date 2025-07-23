// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Handler;
import uk.autores.processing.testing.HandlerResults;
import uk.autores.processing.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateConstantsFromPropertiesTest {

    private final Handler handler = new GenerateConstantsFromProperties();

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(CfgVisibility.DEF));
    }

    @Test
    void canGenerateConstants() throws Exception {
        byte[] legalProperties = ("today={0} said \"Today is {1,date}!\"\n"
                + "\"foo\"=bar\n").getBytes(StandardCharsets.UTF_8);

        HandlerResults hr = tester()
                .withResource("Consts.properties", legalProperties)
                .test();

        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void reportsBadFilename() throws Exception {
        tester().withBadFilename("wrong.dat").test().assertErrorMessagesReported();
    }

    @Test
    void reportsBadNameGeneration() throws Exception {
        tester().withBadFilename("true.properties").test().assertErrorMessagesReported();
    }

    @Test
    void reportsInvalidKeyIdentifier() throws Exception {
        String data = "public=foo";
        tester().withResource("Foo.properties", data.getBytes(StandardCharsets.UTF_8))
                .test()
                .assertErrorMessagesReported();
    }

    @Test
    void generatesBundleName() {
        {
            CharSequence expected = "com.bar.i18n";
            CharSequence actual = GenerateConstantsFromProperties.bundleName("com.foo", "/com/bar/i18n.properties");
            assertEquals(expected, actual.toString());
        }
        {
            CharSequence expected = "com.foo.i18n";
            CharSequence actual = GenerateConstantsFromProperties.bundleName("com.foo", "i18n.properties");
            assertEquals(expected, actual.toString());
        }
    }
}
