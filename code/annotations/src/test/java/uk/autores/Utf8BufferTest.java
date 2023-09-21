package uk.autores;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class Utf8BufferTest {

    /** Bridge at night emoji */
    private static final String SURROGATE_PAIR = new String(Character.toChars(0x1F309));
    private static final String EURO = "\u20AC";
    private static final String MATH_FRACTURE_G = new String(Character.toChars(0x1D50A));
    private static final String POUND = "\u00A3";

    @Test
    void doesNotSplitSurrogatePairs() throws IOException {
        Utf8Buffer buf = Utf8Buffer.size(4);
        Reader src =  new StringReader("123" + SURROGATE_PAIR);

        buf.receive(src);

        assertEquals(3, buf.length());
        assertEquals("123", buf.toString());

        buf.receive(src);

        assertEquals(2, buf.length());
        assertEquals(SURROGATE_PAIR, buf.toString());
    }

    @Test
    void doesNotExceedUtf8Limit() throws IOException {
        String src = IntStream.range(0, 1024)
                .mapToObj(i -> " abcdef" + EURO + SURROGATE_PAIR + MATH_FRACTURE_G + POUND)
                .collect(Collectors.joining());
        Reader reader = new StringReader(src);

        int maxUtf8Len = 4;
        Utf8Buffer buf = Utf8Buffer.size(maxUtf8Len);

        while(buf.receive(reader)) {
            int utfLen = buf.toString().getBytes(StandardCharsets.UTF_8).length;

            assertEquals(utfLen, buf.utf8Length());
            assertTrue(utfLen <= 4);
        }
    }

    @Test
    void throwsOnMalformed() {
        String highSurrogate = SURROGATE_PAIR.substring(0, 1);
        Reader src = new StringReader(highSurrogate);
        Utf8Buffer buf = Utf8Buffer.size(4);

        assertThrowsExactly(IOException.class, () -> buf.receive(src));
    }

    @Test
    void canCreateReadData() throws IOException {
        Utf8Buffer buf = Utf8Buffer.size(1024);

        boolean received = buf.receive(new StringReader("123"));
        assertTrue(received);

        assertEquals(3, buf.length());
        assertEquals('1', buf.charAt(0));
        assertEquals('3', buf.charAt(2));

        received = buf.receive(new StringReader(""));
        assertFalse(received);
        assertEquals(0, buf.length());
    }

    @Test
    void canCreateSubsequence() throws IOException {
        Utf8Buffer buf = Utf8Buffer.size(1024);

        assertEquals("", buf.toString());
        assertEquals("", buf.subSequence(0, 0));

        buf.receive(new StringReader("123"));

        assertEquals("123", buf.toString());
        assertEquals("2", buf.subSequence(1, 2));
    }

    @Test
    void checksBounds() throws IOException {
        Utf8Buffer buf = Utf8Buffer.size(1024);
        buf.receive(new StringReader("123"));

        assertThrowsExactly(IndexOutOfBoundsException.class, () -> buf.charAt(100));
    }
}
