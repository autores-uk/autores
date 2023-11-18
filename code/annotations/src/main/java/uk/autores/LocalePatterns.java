package uk.autores;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSortedMap;

final class LocalePatterns {

    private SortedMap<String, String[]> relationships;

    private Map<String, String[]> relationships() {
        if (relationships == null) {
            ResourceBundle.Control ctrl = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
            relationships = new TreeMap<>();
            for (Locale l : Locale.getAvailableLocales()) {
                if (l.getLanguage().isEmpty()) {
                    continue;
                }
                String pattern = ctrl.toBundleName("", l);
                String[] candidates = candidates(ctrl, l, pattern);
                relationships.put(pattern, candidates);
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
        List<T> candidates = new ArrayList<>(matchers.length);
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
        List<String> list = new ArrayList<>(cl.size());
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
}
