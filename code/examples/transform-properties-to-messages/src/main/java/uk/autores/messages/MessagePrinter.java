package uk.autores.messages;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

public interface MessagePrinter {

    void print(PrintStream out, Locale l, TimeZone tz, Instant now);
}
