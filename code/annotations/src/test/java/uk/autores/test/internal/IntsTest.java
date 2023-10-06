package uk.autores.test.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.internal.Ints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IntsTest {

    @Test
    void intToString() {
        Assertions.assertEquals("0", Ints.toString(0));
        assertSame(Ints.toString(0), Ints.toString(0));
        assertEquals(Byte.toString(Byte.MAX_VALUE), Ints.toString(Byte.MAX_VALUE));
        assertEquals(Byte.toString(Byte.MIN_VALUE), Ints.toString(Byte.MIN_VALUE));
        assertEquals(Integer.toString(Byte.MIN_VALUE - 1), Ints.toString(Byte.MIN_VALUE - 1));
        assertEquals(Integer.toString(Byte.MAX_VALUE + 1), Ints.toString(Byte.MAX_VALUE + 1));
    }
}
