// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Strategy;
import uk.autores.handling.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyTest {

    @Test
    void validates() {
        ConfigDef def = Strategy.DEF;
        for (String enc : asList(Strategy.AUTO, Strategy.INLINE, Strategy.LAZY, Strategy.ENCODE)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
    }
}