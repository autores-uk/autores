// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;
import uk.autores.cfg.MissingKey;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.cfg.MissingKey.MISSING_KEY;

@ResourceFiles(
        value = "sparse.properties",
        handler = GenerateMessagesFromProperties.class,
        // intentionally allow translated files to not contain all the keys the base does
        config = @ResourceFiles.Cfg(key = MISSING_KEY, value = MissingKey.IGNORE),
        // generated names conform to Java norms
        namer = IdiomaticNamer.class
)
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
