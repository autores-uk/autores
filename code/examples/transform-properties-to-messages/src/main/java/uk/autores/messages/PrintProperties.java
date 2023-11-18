package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;
import uk.autores.cfg.Localize;
import uk.autores.cfg.MissingKey;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.cfg.Localize.LOCALIZE;
import static uk.autores.cfg.MissingKey.MISSING_KEY;

@ResourceFiles(
        value = "non-nls.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ResourceFiles.Cfg(key = LOCALIZE, value = Localize.FALSE),
        namer = IdiomaticNamer.class
)
@ResourceFiles(
        value = "messages.properties",
        handler = GenerateMessagesFromProperties.class,
        namer = IdiomaticNamer.class
)
@ResourceFiles(
        value = "sparse.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ResourceFiles.Cfg(key = MISSING_KEY, value = MissingKey.IGNORE),
        namer = IdiomaticNamer.class
)
public class PrintProperties {

    public static void main(String...args)  {
        Locale[] locales = {
                Locale.US,
                Locale.FRENCH,
                Locale.CANADA_FRENCH,
                Locale.GERMANY,
        };
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
