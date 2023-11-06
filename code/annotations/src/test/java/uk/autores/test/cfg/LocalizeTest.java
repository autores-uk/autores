package uk.autores.test.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.Localize;
import uk.autores.processing.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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