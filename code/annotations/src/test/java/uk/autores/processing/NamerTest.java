package uk.autores.processing;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class NamerTest {

    private final Namer namer = new Namer();

    @Test
    void isJavaIdentifier() {
        assertTrue(Namer.isJavaIdentifier("True"));
        assertTrue(Namer.isJavaIdentifier("R123"));
        assertTrue(Namer.isJavaIdentifier("_foo"));
        assertTrue(Namer.isJavaIdentifier("String"));

        assertFalse(Namer.isJavaIdentifier(""));
        assertFalse(Namer.isJavaIdentifier("_"));
        assertFalse(Namer.isJavaIdentifier("if"));
        assertFalse(Namer.isJavaIdentifier("false"));
        assertFalse(Namer.isJavaIdentifier("true"));
        assertFalse(Namer.isJavaIdentifier("8able"));
        assertFalse(Namer.isJavaIdentifier("foo bar"));
    }

    @Test
    void naming() {
        checkNaming(namer::nameClass);
        checkNaming(namer::nameMethod);
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
