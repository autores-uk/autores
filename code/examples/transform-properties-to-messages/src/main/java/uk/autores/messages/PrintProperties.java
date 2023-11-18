package uk.autores.messages;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

public class PrintProperties {

    public static void main(String...args)  {
        Locale[] locales = {
                Locale.US,
                Locale.FRENCH,
                Locale.CANADA_FRENCH,
                Locale.GERMANY,
        };
        // These MessagePrinter implementations use code generated from the properties
        MessagePrinter[] printers = {
                new Translated(),
                new PartlyTranslated(),
                new Untranslated(),
        };

        TimeZone tz = TimeZone.getDefault();
        Instant now = Instant.now();

        for (Locale l : locales) {
            System.out.println();
            System.out.println(l.getDisplayName());
            for (MessagePrinter printer : printers) {
                printer.print(System.out, l, tz, now);
            }
        }
    }
}
