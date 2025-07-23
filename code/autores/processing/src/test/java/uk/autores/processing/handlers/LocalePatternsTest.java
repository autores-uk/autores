// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class LocalePatternsTest {

    @Test
    void shouldReturnPatterns() {
        Set<String> patterns = new LocalePatterns().patterns();
        assertTrue(patterns.contains("_en"));
        assertTrue(patterns.contains("_en_US"));
        assertTrue(patterns.contains("_fr"));
    }

    @Test
    void shouldCacheInInstance() {
        LocalePatterns locales = new LocalePatterns();
        assertSame(locales.patterns(), locales.patterns());
    }

    @Test
    void shouldFindIntermediateProperties() {
        LocalizedThing fr = new LocalizedThing("_fr");
        LocalizedThing de = new LocalizedThing("_de");
        List<LocalizedThing> list = Arrays.asList(de, fr);

        List<LocalizedThing> actual = new LocalePatterns().findCandidatesFor("_fr_CA", lt -> lt.pattern, list);

        List<LocalizedThing> expected = Collections.singletonList(fr);
        assertEquals(expected, actual);
    }

    private interface LP {
        Set<String> patterns();
        <T> List<T> findCandidatesFor(String pattern, Function<T, String> getter, List<T> localizations);
    }

    private static final class LocalizedThing {
        private final String pattern;

        private LocalizedThing(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }
}
