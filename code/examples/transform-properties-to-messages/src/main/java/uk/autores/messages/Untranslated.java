// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;
import uk.autores.cfg.Localize;

import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Locale;

import static uk.autores.cfg.Localize.LOCALIZE;

@ResourceFiles(
        value = "non-nls.properties",
        handler = GenerateMessagesFromProperties.class,
        // this config stops needless search for localized files
        config = @ResourceFiles.Cfg(key = LOCALIZE, value = Localize.FALSE),
        // generated names conform to Java norms
        namer = IdiomaticNamer.class
)
public class Untranslated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, ZonedDateTime time) {
        printAppName(out);
        printFileCounts(out, l);
        printPlanetEvent(out, l, time);
    }

    public void printAppName(PrintStream out) {
        // no formatting, so no need for locale
        String name = NonNls.applicationName();
        out.println(name);
    }

    public void printFileCounts(PrintStream out, Locale l) {
        int[] counts = {0, 1, 1_000_000};
        for (int i : counts) {
            // needs locale because it affects number formats
            String msg = NonNls.fileCount(l, i);
            out.println(msg);
        }
    }

    public void printPlanetEvent(PrintStream out, Locale l, ZonedDateTime time) {
        String event = NonNls.planetEvent(l, 4, time, "an attack");
        out.println(event);
    }
}
