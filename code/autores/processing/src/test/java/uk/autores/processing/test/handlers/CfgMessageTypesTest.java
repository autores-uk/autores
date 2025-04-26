package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.ConfigDef;
import uk.autores.processing.handlers.CfgMessageTypes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CfgMessageTypesTest {

    @Test
    void none() {
        validatesClassNames(CfgMessageTypes.DEF_NONE);
    }

    @Test
    void number() {
        validatesClassNames(CfgMessageTypes.DEF_NUMBER);
    }

    @Test
    void dateTime() {
        validatesClassNames(CfgMessageTypes.DEF_DATE_TIME);
    }

    private void validatesClassNames(ConfigDef def) {
        assertTrue(def.isValid("Foo"));
        assertTrue(def.isValid("bar.Foo"));
        assertTrue(def.isValid("baz.bar.Foo1234"));

        assertFalse(def.isValid("baz.bar.Foo1234?"));
        assertFalse(def.isValid(""));
        assertFalse(def.isValid(" "));
        assertFalse(def.isValid("default"));
    }
}