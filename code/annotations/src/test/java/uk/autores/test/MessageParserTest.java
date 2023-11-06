package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class MessageParserTest {

    private static final String STRING = String.class.getName();
    private static final String NUMBER = Number.class.getName();
    private static final String DATE = Instant.class.getName();

    private final MP parser = Proxies.utility(MP.class, "uk.autores.MessageParser");

    @Test
    void malformedMessageThrowsException() {
        assertThrows(Exception.class, () -> {
            parser.parse("{");
        });
    }

    @Test
    void handlesUnformattedMessage() {
        List<String> actual = parser.parse("Hello, World!");
        assertTrue(actual.isEmpty());
    }

    @Test
    void handlesStringVariable() {
        List<String> actual = parser.parse("Hello, {0}!");
        assertEquals(1, actual.size());
        assertEquals(STRING, actual.get(0));
    }

    @Test
    void handlesNumberVariable() {
        List<String> actual = parser.parse("Hello, {0,number}!");
        assertEquals(1, actual.size());
        assertEquals(NUMBER, actual.get(0));
    }

    @Test
    void handlesDateVariable() {
        List<String> actual = parser.parse("Hello, {0,date}!");
        assertEquals(1, actual.size());
        assertEquals(DATE, actual.get(0));
    }

    @Test
    void handlesVariableOrder() {
        List<String> actual = parser.parse("At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.");
        assertEquals(3, actual.size());
        assertEquals(NUMBER, actual.get(0));
        assertEquals(DATE, actual.get(1));
        assertEquals(STRING, actual.get(2));
    }

    @Test
    void detectsNeedsLocale() {
        assertTrue(parser.needsLocale(singletonList(DATE)));
        assertTrue(parser.needsLocale(singletonList(NUMBER)));
        assertFalse(parser.needsLocale(singletonList(STRING)));
        assertFalse(parser.needsLocale(emptyList()));
    }

    @Test
    void detectsNeedsTimeZone() {
        assertTrue(parser.needsTimeZone(singletonList(DATE)));
        assertFalse(parser.needsTimeZone(singletonList(NUMBER)));
        assertFalse(parser.needsTimeZone(singletonList(STRING)));
        assertFalse(parser.needsTimeZone(emptyList()));
    }

    private interface MP {
        List<String> parse(String s);
        boolean needsTimeZone(List<String> l);
        boolean needsLocale(List<String> l);
    }
}
