// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages.test;

import org.junit.jupiter.api.Test;
import uk.autores.messages.Untranslated;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static uk.autores.messages.test.PrintPropertiesTester.assertOutput;

class UntranslatedTest {

    private final ZonedDateTime epochUtc = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));

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
        String expected = "At 12:00:00 AM on Jan 1, 1970, there was an attack on planet 4.";
        Locale en = Locale.US;
        assertOutput(Untranslated::new, (ps, mp) -> mp.printPlanetEvent(ps, en, epochUtc), expected);
    }

    @Test
    void printPlanetEventGermany() {
        String expected = "At 00:00:00 on 01.01.1970, there was an attack on planet 4.";
        Locale de_DE = Locale.GERMANY;
        assertOutput(Untranslated::new, (ps, mp) -> mp.printPlanetEvent(ps, de_DE, epochUtc), expected);
    }

    @Test
    void printPlanetEventFranceParisTime() {
        String expected = "At 20:35:24 on 1 juin 2023, there was an attack on planet 4.";
        Locale fr_FR = Locale.FRANCE;
        Instant eightPmParisSummerTime = Instant.parse("2023-06-01T18:35:24.00Z");
        ZoneId paris = ZoneId.of("Europe/Paris");
        ZonedDateTime june2023Paris = ZonedDateTime.ofInstant(eightPmParisSummerTime, paris);
        assertOutput(Untranslated::new, (ps, mp) -> mp.printPlanetEvent(ps, fr_FR, june2023Paris), expected);
    }
}
