package uk.autores.cfg;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class EncodingTest {

    @Test
    void validates() {
        for (String enc : Charset.availableCharsets().keySet()) {
            assertTrue(Encoding.DEF.isValid(enc));
        }
        assertFalse(Encoding.DEF.isValid("foobar"));
    }
}
