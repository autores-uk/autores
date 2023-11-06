package uk.autores.test.cfg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.cfg.Name;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NameTest {

    @Test
    void validates() {
        Assertions.assertFalse(Name.DEF.isValid("8"));
        assertTrue(Name.DEF.isValid("foo"));
    }
}