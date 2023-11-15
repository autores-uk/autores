package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnicodeEscapeWriterTest {

    @Test
    void testWrite() throws IOException {
        String input = "Cost: \u00a3200";
        StringWriter buf = new StringWriter();
        try (UEW w = instance(buf)) {
            w.write(input);
            w.flush();
        }

        String expected = "Cost: \\u00a3200";
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    @Test
    void testAppend() throws IOException {
        String input = "Cost: \u20ac200";
        StringWriter buf = new StringWriter();
        try (UEW w = instance(buf)) {
            w.append(input);
        }

        String expected = "Cost: \\u20ac200";
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    @Test
    void testLargerData() throws IOException {
        int rounds = 100_000;
        String input = "foo bar baz";

        StringWriter buf = new StringWriter();
        try (UEW w = instance(buf)) {
            for (int i = 0; i < rounds; i++) {
                w.append(input);
                w.append('\n');
            }
        }

        List<String> expected = IntStream.range(0, rounds).collect(ArrayList::new, (l, i) -> l.add(input), ArrayList::addAll);
        List<String> actual = Arrays.asList(buf.toString().split("\n"));
        assertEquals(expected, actual);
    }

    @Test
    void closeIsIdempotent() throws IOException {
        StringWriter buf = new StringWriter();
        try (UEW w = instance(buf)) {
            w.close();
            w.close();
        }
    }

    @Test
    void throwsWhenClosed() {
        assertThrows(IOException.class, () -> {
                try (UEW w = instance(new StringWriter())) {
                    w.close();
                    w.append("foo");
                }
            }
        );
    }

    @Test
    void testWriteChars() throws IOException {
        char[] input = "Cost: \u20ac200".toCharArray();
        StringWriter buf = new StringWriter();
        try (UEW w = instance(buf)) {
            w.write(input, 0, input.length);
        }

        String expected = "Cost: \\u20ac200";
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    private UEW instance(Writer w) {
        Class<?>[] types = {Writer.class};
        Object[] args = {w};
        return Proxies.instance(UEW.class, "uk.autores.UnicodeEscapeWriter", types, args);
    }

    private interface UEW extends Closeable, Appendable {
        void flush() throws IOException;
        void write(String str) throws IOException;
        void write(char[] array, int off, int len) throws IOException;
    }
}