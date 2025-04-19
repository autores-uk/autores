package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Pkg;

import static org.junit.jupiter.api.Assertions.*;

class PkgTest {

    @Test
    void implementsCharSequence() {
        CharSequenceTests.check("foo.bar", Pkg.named("foo.bar"));
        CharSequenceTests.check("", Pkg.named(""));
    }

    @Test
    void canReturnLastSegment() {
        assertEquals("bar", Pkg.named("bar").lastSegment());
        assertEquals("bar", Pkg.named("foo.bar").lastSegment());
        assertEquals("", Pkg.named("").lastSegment());
    }

    @Test
    void detectsUnnamedPackage() {
        assertFalse(Pkg.named("foo.bar").isUnnamed());
        assertTrue(Pkg.named("").isUnnamed());
    }

    @Test
    void rejectsInvalidNames() {
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("!"));
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("."));
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("a b"));
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("foo."));
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("true"));
        assertThrowsExactly(AssertionError.class, () -> Pkg.named("foo.null"));
    }

    @Test
    void qualifiedClassName() {
        {
            Pkg unnamed = Pkg.named("");
            String expected = "Foo";
            String actual = unnamed.qualifiedClassName(expected);
            assertEquals(expected, actual);
        }
        {
            Pkg unnamed = Pkg.named("bar");
            String simpleName = "Foo";
            String expected = unnamed + "." + simpleName;
            String actual = unnamed.qualifiedClassName(simpleName);
            assertEquals(expected, actual);
        }
    }
}