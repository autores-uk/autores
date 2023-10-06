package uk.autores.internal;

import java.util.stream.IntStream;

/** Utility type for interning certain ints as strings */
public final class Ints {

    private static final String[] BYTES = IntStream.rangeClosed(Byte.MIN_VALUE, Byte.MAX_VALUE)
            .mapToObj(Integer::toString)
            .toArray(String[]::new);

    private Ints() {}

    /**
     * @param n any integer
     * @return integer as base10 string
     */
    public static String toString(int n) {
        if (n >= Byte.MIN_VALUE && n < Byte.MAX_VALUE) {
            return BYTES[n + Byte.MAX_VALUE + 1];
        }
        return Integer.toString(n);
    }
}
