// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.Severity;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.CfgIncompatibleFormat;
import uk.autores.processing.handlers.CfgMissingKey;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgMissingKeyTest {

    @Test
    void validates() {
        ConfigDef def = CfgMissingKey.DEF;
        for (String enc : asList(CfgMissingKey.WARN, CfgMissingKey.IGNORE, CfgMissingKey.ERROR)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
        assertFalse(def.isValid("warning"));
    }

    @Test
    void matches() {
        for (String enc : asList(CfgIncompatibleFormat.WARN, CfgIncompatibleFormat.IGNORE, CfgIncompatibleFormat.ERROR)) {
            boolean found = Stream.of(Severity.values())
                    .map(Severity::value)
                    .anyMatch(enc::equals);
            assertTrue(found);
        }
    }
}