package uk.autores.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import static org.junit.jupiter.api.Assertions.*;

class VisibilityTest {

    @Test
    void validates() {
        ConfigDef def = Visibility.DEF;
        assertTrue(def.isValid(Visibility.PUBLIC));
        assertFalse(def.isValid("foobar"));
    }
}