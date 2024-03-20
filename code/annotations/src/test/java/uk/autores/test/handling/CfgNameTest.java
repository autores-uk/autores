// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.handling.CfgName;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgNameTest {

    @Test
    void validates() {
        Assertions.assertFalse(CfgName.DEF.isValid("8"));
        assertTrue(CfgName.DEF.isValid("foo"));
    }
}