// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainPrintProperties {

    public static void main(String...args)  {
        var places = new LinkedHashMap<Locale, ZoneId>();
        places.put(Locale.getDefault(), ZoneId.systemDefault());
        places.put(Locale.US, ZoneId.of("America/New_York"));
        places.put(Locale.FRANCE, ZoneId.of("Europe/Paris"));
        places.put(Locale.CANADA_FRENCH, ZoneId.of("America/Toronto"));
        places.put(Locale.forLanguageTag("ga"), ZoneId.of("Europe/Dublin"));
        places.put(Locale.GERMANY, ZoneId.of("Europe/Berlin"));
        // These MessagePrinter implementations use code generated from the properties
        MessagePrinter[] printers = {
                new Translated(),
                new PartlyTranslated(),
                new Untranslated(),
        };

        Instant now = Instant.now();

        for (var place : places.entrySet()) {
            Locale l = place.getKey();
            ZoneId zone = place.getValue();

            var time = ZonedDateTime.ofInstant(now, zone);

            System.out.println();
            System.out.println(l.getDisplayName() + " " + zone);
            for (MessagePrinter printer : printers) {
                printer.print(System.out, l, time);
            }
        }
    }
}
