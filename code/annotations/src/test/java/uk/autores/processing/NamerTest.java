package uk.autores.processing;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class NamerTest {

    private final Namer namer = new Namer();

    @Test
    void isJavaIdentifier() {
        assertTrue(Namer.isIdentifier("True"));
        assertTrue(Namer.isIdentifier("R123"));
        assertTrue(Namer.isIdentifier("_foo"));
        assertTrue(Namer.isIdentifier("String"));

        assertFalse(Namer.isIdentifier(""));
        assertFalse(Namer.isIdentifier("_"));
        assertFalse(Namer.isIdentifier("if"));
        assertFalse(Namer.isIdentifier("false"));
        assertFalse(Namer.isIdentifier("true"));
        assertFalse(Namer.isIdentifier("8able"));
        assertFalse(Namer.isIdentifier("foo bar"));
    }

    @Test
    void naming() {
        checkNaming(namer::nameType);
        checkNaming(namer::nameMember);
        checkNaming(namer::nameStaticField);
    }

    private void checkNaming(Function<String, String> fn) {
        assertEquals("Foo", fn.apply("Foo"));
        assertEquals("foo", fn.apply("foo"));
        assertEquals("foo_bar", fn.apply("foo.bar"));
    }

    @Test
    void simplifyResourceName() {
        assertEquals("foo", namer.simplifyResourceName("foo"));
        assertEquals("foo", namer.simplifyResourceName("foo/foo"));
        assertEquals("foo", namer.simplifyResourceName("foo.bar"));
        assertEquals("foo", namer.simplifyResourceName("foo/foo.bar"));
        assertEquals("foo", namer.simplifyResourceName("bar/baz/foo.bar"));
        assertEquals("Foo.bar", namer.simplifyResourceName("bar/baz/Foo.bar.baz"));
    }
}
