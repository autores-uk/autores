// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringLiteralsTest {

    @Test
    void escapesStrings() throws IOException {
        StringWriter sw = new StringWriter();
        StringLiterals.write("\t \" \r \n \\ \f", sw);
        assertEquals("\\t \\\" \\r \\n \\\\ \\f", sw.toString());
    }
}
