// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.Severity;
import uk.autores.Strategy;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.CfgIncompatibleFormat;
import uk.autores.processing.handlers.CfgStrategy;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgStrategyTest {

    @Test
    void validates() {
        ConfigDef def = CfgStrategy.DEF;
        for (String enc : asList(CfgStrategy.AUTO, CfgStrategy.INLINE, CfgStrategy.LAZY, CfgStrategy.CONST)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
        assertFalse(def.isValid("autobot"));
    }

    @Test
    void matches() {
        for (String enc : asList(CfgStrategy.AUTO, CfgStrategy.INLINE, CfgStrategy.LAZY, CfgStrategy.CONST)) {
            boolean found = Stream.of(Strategy.values())
                    .map(Strategy::value)
                    .anyMatch(enc::equals);
            assertTrue(found, enc);
        }
    }
}
