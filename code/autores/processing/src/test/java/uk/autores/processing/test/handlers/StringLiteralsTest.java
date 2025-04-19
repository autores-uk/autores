// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.processing.test.testing.Proxies;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringLiteralsTest {

    @Test
    void escapesStrings() throws IOException {
        SL StringLiterals = Proxies.utility(SL.class, "uk.autores.processing.handlers.StringLiterals");

        StringWriter sw = new StringWriter();
        StringLiterals.write("\t \" \r \n \\ \f", sw);
        assertEquals("\\t \\\" \\r \\n \\\\ \\f", sw.toString());
    }

    private interface SL {
        void write(CharSequence cs, Writer w) throws IOException;
    }
}
