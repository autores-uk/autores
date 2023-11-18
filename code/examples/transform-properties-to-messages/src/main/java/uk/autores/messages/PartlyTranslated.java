package uk.autores.messages;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

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
