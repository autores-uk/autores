// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages.test;

import org.junit.jupiter.api.Test;
import uk.autores.messages.Translated;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.messages.test.PrintPropertiesTester.assertOutput;

class TranslatedTest {

    @Test
    void printHelloWorld() {
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloWorld(ps, Locale.ENGLISH), "Hello, World!");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloWorld(ps, Locale.FRENCH), "Bonjour, francophones !");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloWorld(ps, Locale.CANADA_FRENCH), "Bonjour, Canada!");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloWorld(ps, Locale.GERMANY), "Hello, World!");
    }

    @Test
    void printHelloX() {
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloX(ps, Locale.US), "Hello, Pollux!");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloX(ps, Locale.FRANCE), "Bonjour, Pollux !");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloX(ps, Locale.CANADA_FRENCH), "Salut, Pollux!");
        assertOutput(Translated::new, (ps, mp) -> mp.printHelloX(ps, Locale.GERMANY), "Hello, Pollux!");
    }

    @Test
    void printTodayIs() {
        Instant now = Instant.EPOCH;
        TimeZone gmt = TimeZone.getTimeZone("gmt");
        assertOutput(Translated::new, (ps, mp) -> mp.printTodayIs(ps, Locale.US, gmt, now), "Today is Thursday.");
        assertOutput(Translated::new, (ps, mp) -> mp.printTodayIs(ps, Locale.FRANCE, gmt, now), "Aujourd'hui, c'est jeudi .");
        assertOutput(Translated::new, (ps, mp) -> mp.printTodayIs(ps, Locale.CANADA_FRENCH, gmt, now), "Nous sommes jeudi aujourd'hui.");
        assertOutput(Translated::new, (ps, mp) -> mp.printTodayIs(ps, Locale.GERMANY, gmt, now), "Today is Donnerstag.");
    }
}