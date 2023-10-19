package uk.autores.processors.internal;

/** Utility type for working with {@link java.lang.CharSequence} instances */
public final class CharSeq {

    private CharSeq() {}

    public static boolean equivalent(CharSequence cs1, CharSequence cs2) {
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
