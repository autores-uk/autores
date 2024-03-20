// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class LocalePatternsTest {

    @Test
    void shouldReturnPatterns() {
        Set<String> patterns = instance().patterns();
        assertTrue(patterns.contains("_en"));
        assertTrue(patterns.contains("_en_US"));
        assertTrue(patterns.contains("_fr"));
    }

    @Test
    void shouldCacheInInstance() {
        LP locales = instance();
        assertSame(locales.patterns(), locales.patterns());
    }

    @Test
    void shouldFindIntermediateProperties() {
        LocalizedThing fr = new LocalizedThing("_fr");
        LocalizedThing de = new LocalizedThing("_de");
        List<LocalizedThing> list = Arrays.asList(de, fr);

        List<LocalizedThing> actual = instance().findCandidatesFor("_fr_CA", lt -> lt.pattern, list);

        List<LocalizedThing> expected = Collections.singletonList(fr);
        assertEquals(expected, actual);
    }

    private LP instance() {
        return Proxies.instance(LP.class, "uk.autores.handling.LocalePatterns")
                .params().args();
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
