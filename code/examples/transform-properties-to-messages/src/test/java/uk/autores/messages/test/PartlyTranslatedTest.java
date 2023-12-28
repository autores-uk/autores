// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages.test;

import org.junit.jupiter.api.Test;
import uk.autores.messages.PartlyTranslated;

import java.util.Locale;

import static java.util.Locale.*;
import static uk.autores.messages.test.PrintPropertiesTester.assertOutput;

class PartlyTranslatedTest {

    @Test
    void printLibertyEqualityFraternity() {
        Locale irish = Locale.forLanguageTag("ga");
        assertOutput(PartlyTranslated::new, (ps, mp) -> mp.printLibertyEqualityFraternity(ps, ENGLISH), "Liberty, equality, fraternity.");
        assertOutput(PartlyTranslated::new, (ps, mp) -> mp.printLibertyEqualityFraternity(ps, irish), "Saoirse, comhionannas, br\u00E1ithreachas.");
        assertOutput(PartlyTranslated::new, (ps, mp) -> mp.printLibertyEqualityFraternity(ps, FRENCH), "Libert\u00E9, \u00E9galit\u00E9, fraternit\u00E9.");
        // last should fall back to _fr since it is absent from fr_CA
        assertOutput(PartlyTranslated::new, (ps, mp) -> mp.printLibertyEqualityFraternity(ps, CANADA_FRENCH), "Libert\u00E9, \u00E9galit\u00E9, fraternit\u00E9.");
    }
}