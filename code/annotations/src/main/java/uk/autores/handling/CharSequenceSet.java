package uk.autores.handling;

import java.util.ArrayList;
import java.util.List;

/**
 * Specialized set type.
 */
final class CharSequenceSet {

    private final CharSequence[][] sequences;

    CharSequenceSet(CharSequence...words) {
        int max = 0;
        for (CharSequence word : words) {
            max = Math.max(max, word.length());
        }
        CharSequence[][] entries = new CharSequence[max + 1][0];
        for (int i = 0; i < entries.length; i++) {
            List<CharSequence> lenmatches = new ArrayList<>();
            for (CharSequence word : words) {
                if (i == word.length()) {
                    lenmatches.add(word);
                }
            }
            entries[i] = lenmatches.toArray(new CharSequence[0]);
        }

        this.sequences = entries;
    }

    boolean contains(CharSequence cs, int off, int len) {
        if (len >= sequences.length) {
            return false;
        }
        for (CharSequence candidate : sequences[len]) {
            if (sameSequence(candidate, cs, off, len)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameSequence(CharSequence test, CharSequence cs, int off, int len) {
        for (int i = 0, j = off; i < len; i++, j++) {
            if (test.charAt(i) != cs.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
