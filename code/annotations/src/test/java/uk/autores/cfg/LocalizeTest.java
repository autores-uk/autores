package uk.autores.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class LocalizeTest {

    @Test
    void validates() {
        ConfigDef def = Localize.DEF;
        for (String v : asList(Localize.FALSE, Localize.TRUE)) {
            assertTrue(def.isValid(v));
        }
        assertFalse(def.isValid("foobar"));
    }
}