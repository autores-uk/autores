// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.IdiomaticNamer;
import uk.autores.handling.Namer;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdiomaticNamerTest {

    private final Namer n = new IdiomaticNamer();

    @Test
    void nameMember() {
        assertExpectation(n::nameMember, "foo", "Foo");
        assertExpectation(n::nameMember, "foo", "foo");
        assertExpectation(n::nameMember, "fooBarBaz", "foo-bar-baz");
        assertExpectation(n::nameMember, "foo123Bar", "foo-123-bar");
        assertExpectation(n::nameMember, "foo123Bar", "foo123-bar");
        assertExpectation(n::nameMember, "fooBarBaz", "FooBarBaz");
    }

    @Test
    void nameStaticField() {
        assertExpectation(n::nameConstant, "FOO", "Foo");
        assertExpectation(n::nameConstant, "FOO", "foo");
        assertExpectation(n::nameConstant, "FOO_BAR_BAZ", "foo-bar-baz");
        assertExpectation(n::nameConstant, "FOO_123_BAR", "foo-123-bar");
        assertExpectation(n::nameConstant, "FOO123_BAR", "foo123-bar");
        assertExpectation(n::nameConstant, "FOO_BAR_BAZ", "FooBarBaz");
    }

    @Test
    void nameType() {
        assertExpectation(n::nameType, "Foo", "Foo");
        assertExpectation(n::nameType, "Foo", "foo");
        assertExpectation(n::nameType, "FooBarBaz", "foo-bar-baz");
        assertExpectation(n::nameType, "Foo123Bar", "foo-123-bar");
        assertExpectation(n::nameType, "Foo123Bar", "foo123-bar");
        assertExpectation(n::nameType, "FooBarBaz", "FooBarBaz");
    }

    private void assertExpectation(Function<String, String> namer, String expected, String source) {
        String actual = namer.apply(source);
        assertEquals(expected, actual);
    }
}