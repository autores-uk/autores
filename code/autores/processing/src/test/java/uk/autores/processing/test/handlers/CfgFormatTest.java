// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.CfgFormat;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgFormatTest {

    @Test
    void validates() {
        ConfigDef def = CfgFormat.DEF;
        for (String v : asList(CfgFormat.FALSE, CfgFormat.TRUE)) {
            assertTrue(def.isValid(v));
        }
        assertFalse(def.isValid("foobar"));
    }
}