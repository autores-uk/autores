package uk.autores.processors.internal;

/** Utility type for comparing certain types. */
public final class Compare {

    private Compare() {}

    public static <C extends Comparable<C>> C max(C a, C b) {
        int n = a.compareTo(b);
        return n > 0 ? a : b;
    }

    public static boolean sameSeq(CharSequence cs1, CharSequence cs2) {
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

    public static boolean nullOrEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
