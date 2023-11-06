package uk.autores.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IntsTest {

    @Test
    void intToString() {
        Ints ints = Proxies.utility(Ints.class, "uk.autores.Ints");

        Assertions.assertEquals("0", ints.toString(0));
        assertSame(ints.toString(0), ints.toString(0));
        assertEquals(Byte.toString(Byte.MAX_VALUE), ints.toString(Byte.MAX_VALUE));
        assertEquals(Byte.toString(Byte.MIN_VALUE), ints.toString(Byte.MIN_VALUE));
        assertEquals(Integer.toString(Byte.MIN_VALUE - 1), ints.toString(Byte.MIN_VALUE - 1));
        assertEquals(Integer.toString(Byte.MAX_VALUE + 1), ints.toString(Byte.MAX_VALUE + 1));
    }

    private interface Ints {
        String toString(int n);
    }
}
