// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import uk.autores.MessageResources;
import uk.autores.Severity;

import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Locale;

@MessageResources(value = "sparse.properties", missingKey = Severity.IGNORE)
public class PartlyTranslated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, ZonedDateTime time) {
        printLibertyEqualityFraternity(out, l);
    }

    public void printLibertyEqualityFraternity(PrintStream out, Locale l) {
        // for the "fr_CA" locale the "fr" string is returned
        String msg = Sparse.liberty(l);
        out.println(msg);
    }
}
