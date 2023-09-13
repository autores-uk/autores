package demo;

import org.junit.jupiter.api.Test;

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
