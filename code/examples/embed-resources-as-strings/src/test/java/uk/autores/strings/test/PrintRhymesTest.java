// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strings.test;

import org.junit.jupiter.api.Test;
import uk.autores.strings.Poule;
import uk.autores.strings.PrintRhymes;
import uk.autores.strings.Roses;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PrintRhymesTest {

    @org.junit.jupiter.api.Test
    void main() {
        PrintRhymes.main();
    }

    @Test
    void roses() {
        String actual = Roses.text();
        assertTrue(actual.endsWith("We all fall down!"));
    }

    @Test
    void poule() {
        String actual = Poule.text();
        assertTrue(actual.endsWith("L\u00e8ve la queue\nEt puis s\u2019en va."));
    }
}
