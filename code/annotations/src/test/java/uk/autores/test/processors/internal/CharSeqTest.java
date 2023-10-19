package uk.autores.test.processors.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.processors.internal.CharSeq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharSeqTest {

    @Test
    void equivalent() throws Exception {


        Assertions.assertTrue(CharSeq.equivalent("a", "a"));
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
