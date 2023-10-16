package uk.autores.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class MissingKeyTest {

    @Test
    void validates() {
        ConfigDef def = MissingKey.DEF;
        for (String enc : asList(MissingKey.WARN, MissingKey.IGNORE, MissingKey.ERROR)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
    }

}