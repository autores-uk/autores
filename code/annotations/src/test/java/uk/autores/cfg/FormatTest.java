package uk.autores.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatTest {

    @Test
    void validates() {
        ConfigDef def = Format.DEF;
        for (String v : asList(Format.FALSE, Format.TRUE)) {
            assertTrue(def.isValid(v));
        }
        assertFalse(def.isValid("foobar"));
    }
}