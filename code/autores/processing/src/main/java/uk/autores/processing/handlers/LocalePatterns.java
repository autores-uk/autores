// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSortedMap;

final class LocalePatterns {

    private SortedMap<String, String[]> relationships;
    private final Map<String, Locale> locales = new HashMap<>();

    private Map<String, String[]> relationships() {
        if (relationships == null) {
            var ctrl = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
            relationships = new TreeMap<>();
            for (var l : Locale.getAvailableLocales()) {
                if (l.getLanguage().isEmpty()) {
                    continue;
                }
                String pattern = ctrl.toBundleName("", l);
                String[] candidates = candidates(ctrl, l, pattern);
                relationships.put(pattern, candidates);
                locales.put(pattern, l);
            }
            relationships = unmodifiableSortedMap(relationships);
        }
        return relationships;
    }

    Set<String> patterns() {
        return relationships().keySet();
    }

    <T> List<T> findCandidatesFor(String pattern, Function<T, String> getter, List<T> localizations) {
        String[] matchers = relationships().get(pattern);
        if (matchers == null) {
            throw new AssertionError("Unknown pattern: " + pattern);
        }
        if (matchers.length == 0) {
            return Collections.emptyList();
        }
        var candidates = new ArrayList<T>(matchers.length);
        outer: for (String matcher : matchers) {
            for (T candidate : localizations) {
                String p = getter.apply(candidate);
                if (p.equals(matcher)) {
                    candidates.add(candidate);
                    continue outer;
                }
            }
        }
        return candidates;
    }

    private String[] candidates(ResourceBundle.Control ctrl, Locale l, String pattern) {
        List<Locale> cl = ctrl.getCandidateLocales("", l);
        var list = new ArrayList<String>(cl.size());
        for (Locale candidate : cl) {
            if (candidate.getLanguage().isEmpty()) {
                continue;
            }
            String candidatePattern = ctrl.toBundleName("", candidate);
            if (candidatePattern.equals(pattern)) {
                continue;
            }
            list.add(candidatePattern);
        }
        return list.toArray(new String[0]);
    }

    Locale locale(String pattern) {
        return locales.getOrDefault(pattern, Locale.US);
    }
}
