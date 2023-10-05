package uk.autores.test.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.internal.CharSeq;

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
}
