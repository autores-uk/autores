package uk.autores.processors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparablesTest {

    @Test
    void max() {
        int expected = 10;
        int actual = Comparables.max(1, expected);
        assertEquals(expected, actual);
        actual = Comparables.max(expected, 1);
        assertEquals(expected, actual);
    }
}
