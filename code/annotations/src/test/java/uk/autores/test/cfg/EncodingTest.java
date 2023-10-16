package uk.autores.test.cfg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.cfg.Encoding;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncodingTest {

    @Test
    void validates() {
        for (String enc : Charset.availableCharsets().keySet()) {
            Assertions.assertTrue(Encoding.DEF.isValid(enc));
        }
        assertFalse(Encoding.DEF.isValid("foobar"));
    }
}
