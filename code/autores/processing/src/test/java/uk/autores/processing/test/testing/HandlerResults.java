// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.testing;

import org.joor.Reflect;
import uk.autores.processing.test.testing.env.TestFileObject;
import uk.autores.processing.test.testing.env.TestProcessingEnvironment;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class HandlerResults {

    private final TestProcessingEnvironment env;

    HandlerResults(TestProcessingEnvironment env) {
        this.env = env;
    }

    private List<CharSequence> errors() {
        return env.getMessager().messages.get(Diagnostic.Kind.ERROR);
    }

    public void assertErrorMessagesReported() {
        assertFalse(errors().isEmpty());
    }

    public void assertNoErrorMessagesReported() {
        assertTrue(errors().isEmpty(), errors().toString());
    }

    public void assertAllGeneratedFilesCompile(int expected) {
        Map<String, TestFileObject> sources = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT);
        assertEquals(expected, sources.size(), sources.keySet().toString());

        String utilityType = "";
        String utilitySrc = "";
        for (Map.Entry<String, TestFileObject> entry : sources.entrySet()) {
            if (entry.getKey().startsWith("AutoRes$")) {
                utilityType = entry.getKey();
                utilitySrc = new String(entry.getValue().data.toByteArray(), StandardCharsets.UTF_8);
            }
        }

        for (Map.Entry<String, TestFileObject> entry : sources.entrySet()) {
            String qualifiedClassName = entry.getKey();
            if (qualifiedClassName.equals(utilityType)) {
                continue;
            }

            TestFileObject file = entry.getValue();
            String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);

            Reflect.compile(
                    qualifiedClassName,
                    src + System.lineSeparator() + utilitySrc
            ).create().get();
        }
    }

    public Map<String, String> generatedSource() {
        Map<String, String> r = new HashMap<>();
        Map<String, TestFileObject> sources = env.getFiler().files.get(StandardLocation.SOURCE_OUTPUT);
        for (Map.Entry<String, TestFileObject> entry : sources.entrySet()) {
            String qualifiedClassName = entry.getKey();
            TestFileObject file = entry.getValue();
            String src = new String(file.data.toByteArray(), StandardCharsets.UTF_8);
            r.put(qualifiedClassName, src);
        }
        return r;
    }
}
