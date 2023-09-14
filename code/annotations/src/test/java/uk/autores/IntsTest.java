package uk.autores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntsTest {

    @Test
    void intToString() {
        assertEquals("0", Ints.toString(0));
        assertSame(Ints.toString(0), Ints.toString(0));
        assertEquals(Byte.toString(Byte.MAX_VALUE), Ints.toString(Byte.MAX_VALUE));
        assertEquals(Byte.toString(Byte.MIN_VALUE), Ints.toString(Byte.MIN_VALUE));
        assertEquals(Integer.toString(Byte.MIN_VALUE - 1), Ints.toString(Byte.MIN_VALUE - 1));
        assertEquals(Integer.toString(Byte.MAX_VALUE + 1), Ints.toString(Byte.MAX_VALUE + 1));
    }
}
