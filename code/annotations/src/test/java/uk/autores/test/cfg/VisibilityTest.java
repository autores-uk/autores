package uk.autores.test.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Visibility;
import uk.autores.processing.ConfigDef;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibilityTest {

    @Test
    void validates() {
        ConfigDef def = Visibility.DEF;
        assertTrue(def.isValid(Visibility.PUBLIC));
        assertFalse(def.isValid("foobar"));
    }
}