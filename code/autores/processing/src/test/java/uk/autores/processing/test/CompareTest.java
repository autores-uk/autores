// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.autores.processing.test.testing.Proxies;

import static org.junit.jupiter.api.Assertions.*;

class CompareTest {

    private final CProxy Compare = Proxies.utility(CProxy.class, "uk.autores.processing.Compare");

    @Test
    void equivalent() {
        Assertions.assertTrue(Compare.sameSeq("a", "a"));
        assertTrue(Compare.sameSeq("a", new StringBuilder("a")));
        assertFalse(Compare.sameSeq("a", "ab"));
        assertFalse(Compare.sameSeq("a", "b"));
    }

    @Test
    void testsNullOrEmpty() {
        assertTrue(Compare.nullOrEmpty(null));
        assertTrue(Compare.nullOrEmpty(""));
        assertFalse(Compare.nullOrEmpty("a"));
    }

    @Test
    void max() {
        int expected = 10;
        int actual = Compare.max(1, expected);
        assertEquals(expected, actual);
        actual = Compare.max(expected, 1);
        assertEquals(expected, actual);
    }

    private interface CProxy {
        boolean sameSeq(CharSequence cs1, CharSequence cs2);
        boolean nullOrEmpty(CharSequence cs);
        <C extends Comparable<C>> C max(C a, C b);
    }
}
