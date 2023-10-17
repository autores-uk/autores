package uk.autores.processing;

import static org.junit.jupiter.api.Assertions.*;

class CharSequenceTests {

    private CharSequenceTests() {}

    static void check(String expected, CharSequence actual) {
        assertEquals(expected.length(), actual.length());
        for (int i = 0; i < expected.length(); i++) {
            assertEquals(expected.charAt(i), actual.charAt(i));
        }
        assertEquals(expected, actual.toString());

        int half = expected.length() / 2;
        assertEquals(expected.substring(0, half), actual.subSequence(0, half).toString());
    }
}
