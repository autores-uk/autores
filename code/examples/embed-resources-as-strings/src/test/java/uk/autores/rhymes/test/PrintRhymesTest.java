// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.rhymes.test;

import org.junit.jupiter.api.Test;
import uk.autores.rhymes.PrintRhymes;
import uk.autores.rhymes.Rhymes;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PrintRhymesTest {

    @Test
    void main() {
        PrintRhymes.main();
    }

    @Test
    void roses() {
        String actual = Rhymes.roses();
        assertTrue(actual.endsWith("We all fall down!"));
    }

    @Test
    void poule() {
        String actual = Rhymes.poule();
        assertTrue(actual.endsWith("L\u00e8ve la queue\nEt puis s\u2019en va."));
    }
}
