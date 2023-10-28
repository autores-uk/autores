package uk.autores.cfg;

import org.junit.jupiter.api.Test;
import uk.autores.processing.ConfigDef;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyTest {

    @Test
    void validates() {
        ConfigDef def = Strategy.DEF;
        for (String enc : asList(Strategy.AUTO, Strategy.INLINE, Strategy.LAZY, Strategy.ENCODE)) {
            assertTrue(def.isValid(enc));
        }
        assertFalse(def.isValid("foobar"));
    }
}