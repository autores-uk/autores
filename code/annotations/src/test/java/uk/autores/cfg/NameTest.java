package uk.autores.cfg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NameTest {

    @Test
    void validates() {
        assertFalse(Name.DEF.isValid("8"));
        assertTrue(Name.DEF.isValid("foo"));
    }
}