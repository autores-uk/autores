// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.processing.handlers.CfgEncoding;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class CfgEncodingTest {

    @Test
    void validates() {
        for (String enc : Charset.availableCharsets().keySet()) {
            Assertions.assertTrue(CfgEncoding.DEF.isValid(enc));
        }
        assertFalse(CfgEncoding.DEF.isValid("foobar"));
    }
}
