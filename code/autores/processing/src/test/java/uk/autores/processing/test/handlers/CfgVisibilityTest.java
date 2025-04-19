// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.CfgVisibility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgVisibilityTest {

    @Test
    void validates() {
        ConfigDef def = CfgVisibility.DEF;
        assertTrue(def.isValid(CfgVisibility.PUBLIC));
        assertFalse(def.isValid("foobar"));
    }
}