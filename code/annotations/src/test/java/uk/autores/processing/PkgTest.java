package uk.autores.processing;

import org.junit.jupiter.api.Test;
import uk.autores.processing.Pkg;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PkgTest {

    @Test
    void nameForLookup() {
        assertEquals("foo.bar", new Pkg("foo.bar", true).resourcePackage());
        assertEquals("", new Pkg("foo.bar", false).resourcePackage());
    }
}
