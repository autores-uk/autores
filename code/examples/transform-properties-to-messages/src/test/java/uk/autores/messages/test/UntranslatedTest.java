// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages.test;

import org.junit.jupiter.api.Test;
import uk.autores.messages.Untranslated;

import java.util.Locale;
import java.util.TimeZone;

import static uk.autores.messages.test.PrintPropertiesTester.assertOutput;

class UntranslatedTest {

    @Test
    void printAppName() {
        assertOutput(Untranslated::new, (ps, mp) -> mp.printAppName(ps), "PrintProperties");
    }

    @Test
    void printFileCounts() {
        String[] expected = {
                "There are no files.",
                "There is one file.",
                "There are 1,000,000 files.",

        };
        Locale en = Locale.US;
        assertOutput(Untranslated::new, (ps, mp) -> mp.printFileCounts(ps, en), expected);
    }

    @Test
    void printPlanetEvent() {
        String expected = "At 00:00:00 on 01-Jan-1970, there was an attack on planet 4.";
        Locale en = Locale.UK;
        TimeZone gmt = TimeZone.getTimeZone("gmt");
        assertOutput(Untranslated::new, (ps, mp) -> mp.printPlanetEvent(ps, en, gmt), expected);
    }

    @Test
    void printPlanetEventGermany() {
        String expected = "At 00:00:00 on 01.01.1970, there was an attack on planet 4.";
        Locale en = Locale.GERMANY;
        TimeZone gmt = TimeZone.getTimeZone("gmt");
        assertOutput(Untranslated::new, (ps, mp) -> mp.printPlanetEvent(ps, en, gmt), expected);
    }
}
