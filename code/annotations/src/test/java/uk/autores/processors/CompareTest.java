package uk.autores.processors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompareTest {

    @Test
    void equivalent() {
        assertTrue(Compare.sameSeq("a", "a"));
        assertTrue(Compare.sameSeq("a", new StringBuilder("a")));
        assertFalse(Compare.sameSeq("a", "ab"));
        assertFalse(Compare.sameSeq("a", "b"));
    }

    @Test
    void testsNullOrEmpty() {
        assertTrue(Compare.nullOrEmpty(null));
        assertTrue(Compare.nullOrEmpty(""));
        assertFalse(Compare.nullOrEmpty("a"));
    }

    @Test
    void max() {
        int expected = 10;
        int actual = Compare.max(1, expected);
        assertEquals(expected, actual);
        actual = Compare.max(expected, 1);
        assertEquals(expected, actual);
    }
}
