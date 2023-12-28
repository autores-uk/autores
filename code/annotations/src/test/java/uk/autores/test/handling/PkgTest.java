// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
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
}
