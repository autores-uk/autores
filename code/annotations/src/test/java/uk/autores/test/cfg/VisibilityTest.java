// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Visibility;
import uk.autores.handling.ConfigDef;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityTest {

    @Test
    void validates() {
        ConfigDef def = Visibility.DEF;
        assertTrue(def.isValid(Visibility.PUBLIC));
        assertFalse(def.isValid("foobar"));
    }
}