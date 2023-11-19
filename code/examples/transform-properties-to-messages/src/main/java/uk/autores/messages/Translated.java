package uk.autores.messages;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.IdiomaticNamer;
import uk.autores.ResourceFiles;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

@ResourceFiles(
        value = "messages.properties",
        handler = GenerateMessagesFromProperties.class,
        // generated names conform to Java norms
        namer = IdiomaticNamer.class
)
public class Translated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, TimeZone tz, Instant now) {
        printHelloWorld(out, l);
        printHelloX(out, l);
        printTodayIs(out, l, tz, now);
    }

    public void printHelloWorld(PrintStream out, Locale l) {
        String hello = Messages.helloWorld(l);
        out.println(hello);
    }

    public void printHelloX(PrintStream out, Locale l) {
        String hello = Messages.hello(l, "Pollux");
        out.println(hello);
    }

    public void printTodayIs(PrintStream out, Locale l, TimeZone tz, Instant day) {
        // needs a time zone because the format string includes DateFormat {N,date}
        String msg = Messages.todayIs(l, tz, day);
        out.println(msg);
    }
}
