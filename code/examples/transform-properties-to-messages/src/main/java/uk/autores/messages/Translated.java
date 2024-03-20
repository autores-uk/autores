// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import uk.autores.MessageResources;

import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Locale;

@MessageResources("messages.properties")
public class Translated implements MessagePrinter {

    @Override
    public void print(PrintStream out, Locale l, ZonedDateTime time) {
        printHelloWorld(out, l);
        printHelloX(out, l);
        printTodayIs(out, l, time);
    }

    public void printHelloWorld(PrintStream out, Locale l) {
        String hello = Messages.helloWorld(l);
        out.println(hello);
    }

    public void printHelloX(PrintStream out, Locale l) {
        String hello = Messages.hello(l, "Pollux");
        out.println(hello);
    }

    public void printTodayIs(PrintStream out, Locale l, ZonedDateTime time) {
        // needs a time zone because the format string includes DateFormat {N,date}
        String msg = Messages.todayIs(l, time);
        out.println(msg);
    }
}
