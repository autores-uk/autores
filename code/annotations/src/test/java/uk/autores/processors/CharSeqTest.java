package uk.autores.processors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharSeqTest {

    @Test
    void equivalent() {
        assertTrue(CharSeq.equivalent("a", "a"));
        assertTrue(CharSeq.equivalent("a", new StringBuilder("a")));
        assertFalse(CharSeq.equivalent("a", "ab"));
        assertFalse(CharSeq.equivalent("a", "b"));
    }

    @Test
    void testsNullOrEmpty() {
        assertTrue(CharSeq.nullOrEmpty(null));
        assertTrue(CharSeq.nullOrEmpty(""));
        assertFalse(CharSeq.nullOrEmpty("a"));
    }
}
