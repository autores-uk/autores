// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.CfgIncompatibleFormat;
import uk.autores.handling.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgIncompatibleFormatTest {

    @Test
    void validates() {
        ConfigDef def = CfgIncompatibleFormat.DEF;
        for (String enc : asList(CfgIncompatibleFormat.WARN, CfgIncompatibleFormat.IGNORE, CfgIncompatibleFormat.ERROR)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
    }

}