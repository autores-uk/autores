// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers.debug;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.*;
import uk.autores.processing.test.testing.HandlerResults;
import uk.autores.processing.test.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DebugReportTest {
    private HandlerTester tester(DebugReport handler) {
        return new HandlerTester(handler);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = new DebugReport().config();
        assertTrue(supported.isEmpty());
    }

    @Test
    void canReport() throws Exception {
        String data = "\0\0\0A\0";
        String filename = "foo.txt";

        HandlerResults hr = tester(new DebugReport())
                .withResource(filename, data.getBytes(StandardCharsets.UTF_8))
                .test();
        hr.assertNoErrorMessagesReported();
    }

    @Test
    void handlesMultipleInvocations() throws Exception {
        String data = "\0\0\0A\0";
        String filename = "foo.txt";

        DebugReport dr = new DebugReport();

        HandlerResults hr = tester(dr)
                .withResource(filename, data.getBytes(StandardCharsets.UTF_8))
                .withResource("again" + filename, data.getBytes(StandardCharsets.UTF_8))
                .test();
        hr.assertNoErrorMessagesReported();

        hr = tester(dr)
                .withResource(filename, data.getBytes(StandardCharsets.UTF_8))
                .withResource("again" + filename, data.getBytes(StandardCharsets.UTF_8))
                .test();
        hr.assertNoErrorMessagesReported();
    }

    @Test
    void handlesEmpty() throws Exception {
        HandlerResults hr = tester(new DebugReport())
                .test();
        hr.assertNoErrorMessagesReported();
    }
}
