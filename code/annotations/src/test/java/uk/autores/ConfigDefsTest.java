package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigDefsTest {

    @Test
    void canCreateSet() {
        Set<ConfigDef> actual = ConfigDefs.set(ConfigDefs.LOCALIZE, ConfigDefs.VISIBILITY);
        assertEquals(2, actual.size());
        assertTrue(actual.contains(ConfigDefs.LOCALIZE));
        assertTrue(actual.contains(ConfigDefs.VISIBILITY));
    }

    @Test
    void visibilityIsPublic() {
        ConfigDef cd = ConfigDefs.VISIBILITY;
        assertTrue(cd.isValid("public"));
        assertFalse(cd.isValid("private"));
    }

    @Test
    void encodingIsValid() {
        ConfigDef cd = ConfigDefs.ENCODING;
        assertTrue(cd.isValid("UTF-8"));
        assertFalse(cd.isValid("wibble"));
    }

    @Test
    void localizeIsTrueOrFalse() {
        ConfigDef cd = ConfigDefs.LOCALIZE;
        assertTrue(cd.isValid("true"));
        assertTrue(cd.isValid("false"));
        assertFalse(cd.isValid("foo"));
    }

    @Test
    void missingKeyIsErrorWarnIgnore() {
        ConfigDef cd = ConfigDefs.MISSING_KEY;
        assertTrue(cd.isValid("error"));
        assertTrue(cd.isValid("warn"));
        assertTrue(cd.isValid("ignore"));
        assertFalse(cd.isValid("foo"));
    }

    @Test
    void formatIsTrueOrFalse() {
        ConfigDef cd = ConfigDefs.FORMAT;
        assertTrue(cd.isValid("true"));
        assertTrue(cd.isValid("false"));
        assertFalse(cd.isValid("foo"));
    }
}
