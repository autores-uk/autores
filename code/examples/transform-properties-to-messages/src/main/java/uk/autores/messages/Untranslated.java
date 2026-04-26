// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import uk.autores.Messages;

import java.io.PrintStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Messages(value = "non-nls.properties", localize = false)
public class Untranslated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, ZonedDateTime time) {
        printAppName(out);
        printFileCounts(out, l);
        printPlanetEvent(out, l, time);
        printYouSay(out);
        printTimeInTokyo(out, l, time);
        printRFC1123DateTime(out, l, time);
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

    public void printYouSay(PrintStream out) {
        String saying = NonNls.youSay("tomato");
        out.println(saying);
    }

    public void printTimeInTokyo(PrintStream out, Locale l, ZonedDateTime time) {
        var tokyo = ZoneId.of("Asia/Tokyo");
        var timeInTokyo = time.withZoneSameInstant(tokyo);
        String msg = NonNls.timeInTokyo(l, timeInTokyo, time, time.getZone().getDisplayName(TextStyle.FULL, l));
        out.println(msg);
    }

    public void printRFC1123DateTime(PrintStream out, Locale l, ZonedDateTime time) {
        String s = NonNls.rfc1123DateTime(l, time);
        out.println(s);
    }
}
