// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Namer;

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
    void isJavaPackage() {
        assertTrue(Namer.isPackage(""));
        assertTrue(Namer.isPackage("True"));
        assertTrue(Namer.isPackage("R123"));
        assertTrue(Namer.isPackage("_foo"));
        assertTrue(Namer.isPackage("String"));
        assertTrue(Namer.isPackage("foo.bar"));
        assertTrue(Namer.isPackage("foo.bar.baz"));

        assertFalse(Namer.isPackage("_"));
        assertFalse(Namer.isPackage("if"));
        assertFalse(Namer.isPackage("false"));
        assertFalse(Namer.isPackage("true"));
        assertFalse(Namer.isPackage("8able"));
        assertFalse(Namer.isPackage("foo bar"));
        assertFalse(Namer.isPackage(".foo.bar"));
        assertFalse(Namer.isPackage("foo.bar."));
        assertFalse(Namer.isPackage("."));
        assertFalse(Namer.isPackage("foo..bar"));
    }

    @Test
    void naming() {
        checkNaming(namer::nameType);
        checkNaming(namer::nameMember);
        checkNaming(namer::nameConstant);
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
