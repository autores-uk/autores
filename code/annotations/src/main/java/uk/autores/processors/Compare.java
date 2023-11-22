// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

/** Utility type for comparing certain types. */
final class Compare {

    private Compare() {}

    static <C extends Comparable<C>> C max(C a, C b) {
        int n = a.compareTo(b);
        return n > 0 ? a : b;
    }

    static boolean sameSeq(CharSequence cs1, CharSequence cs2) {
        if (cs1.length() != cs2.length()) {
            return false;
        }
        for (int i = cs1.length() - 1; i >= 0; i--) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    static boolean nullOrEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
