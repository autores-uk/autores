package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;
import uk.autores.cfg.Localize;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.cfg.Localize.LOCALIZE;

@ResourceFiles(
        value = "non-nls.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ResourceFiles.Cfg(key = LOCALIZE, value = Localize.FALSE),
        namer = IdiomaticNamer.class
)
public class Untranslated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, TimeZone tz, Instant now) {
        printAppName(out);
        printFileCounts(out, l);
        printPlanetEvent(out, l, tz);
    }

    public void printAppName(PrintStream out) {
        String name = NonNls.applicationName();
        out.println(name);
    }

    public void printFileCounts(PrintStream out, Locale l) {
        int[] counts = {0, 1, 1_000_000};
        for (int i : counts) {
            String msg = NonNls.fileCount(l, i);
            out.println(msg);
        }
    }

    public void printPlanetEvent(PrintStream out, Locale l, TimeZone tz) {
        String event = NonNls.planetEvent(l, tz, 4, Instant.EPOCH, "an attack");
        out.println(event);
    }
}
