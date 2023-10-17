package uk.autores.test.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.processing.Pkg;

import static org.junit.jupiter.api.Assertions.*;

class PkgTest {

    @Test
    void implementsCharSequence() {
        CharSequenceTests.check("foo.bar", new Pkg("foo.bar"));
        CharSequenceTests.check("", new Pkg(""));
    }

    @Test
    void canReturnLastSegment() {
        assertEquals("bar", new Pkg("bar").lastSegment());
        assertEquals("bar", new Pkg("foo.bar").lastSegment());
        assertEquals("", new Pkg("").lastSegment());
    }

    @Test
    void detectsUnnamedPackage() {
        assertFalse(new Pkg("foo.bar").isUnnamed());
        assertTrue(new Pkg("").isUnnamed());
    }
}
