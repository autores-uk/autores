package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;
import uk.autores.cfg.MissingKey;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.cfg.MissingKey.MISSING_KEY;

@ResourceFiles(
        value = "sparse.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ResourceFiles.Cfg(key = MISSING_KEY, value = MissingKey.IGNORE),
        namer = IdiomaticNamer.class
)
public class PartlyTranslated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, TimeZone tz, Instant now) {
        printLibertyEqualityFraternity(out, l);
    }

    public void printLibertyEqualityFraternity(PrintStream out, Locale l) {
        String msg = Sparse.liberty(l);
        out.println(msg);
    }
}
