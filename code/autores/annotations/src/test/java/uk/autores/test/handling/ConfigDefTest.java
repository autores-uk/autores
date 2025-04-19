package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ConfigDefTest {

    @Test
    void key() {
        String expected = "foo";
        String actual = new ConfigDef(expected, Objects::nonNull).key();
        assertEquals(expected, actual);
    }

    @Test
    void isValid() {
        ConfigDef def = new ConfigDef("foo", "X"::equals);
        assertTrue(def.isValid("X"));
        assertFalse(def.isValid("Y"));
    }
}