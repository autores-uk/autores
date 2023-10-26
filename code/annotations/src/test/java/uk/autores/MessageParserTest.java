package uk.autores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class MessageParserTest {

    @Test
    void malformedMessageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            MessageParser.parse("{");
        });
    }

    @Test
    void handlesUnformattedMessage() {
        List<MessageParser.VarType> actual = MessageParser.parse("Hello, World!");
        assertTrue(actual.isEmpty());
    }

    @Test
    void handlesStringVariable() {
        List<MessageParser.VarType> actual = MessageParser.parse("Hello, {0}!");
        assertEquals(1, actual.size());
        assertEquals(MessageParser.VarType.STRING, actual.get(0));
    }

    @Test
    void handlesNumberVariable() {
        List<MessageParser.VarType> actual = MessageParser.parse("Hello, {0,number}!");
        assertEquals(1, actual.size());
        assertEquals(MessageParser.VarType.NUMBER, actual.get(0));
    }

    @Test
    void handlesDateVariable() {
        List<MessageParser.VarType> actual = MessageParser.parse("Hello, {0,date}!");
        assertEquals(1, actual.size());
        assertEquals(MessageParser.VarType.DATE, actual.get(0));
    }

    @Test
    void handlesVariableOrder() {
        List<MessageParser.VarType> actual = MessageParser.parse("At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.");
        assertEquals(3, actual.size());
        assertEquals(MessageParser.VarType.NUMBER, actual.get(0));
        assertEquals(MessageParser.VarType.DATE, actual.get(1));
        assertEquals(MessageParser.VarType.STRING, actual.get(2));
    }

    @Test
    void detectsNeedsLocale() {
        assertTrue(MessageParser.needsLocale(singletonList(MessageParser.VarType.DATE)));
        assertTrue(MessageParser.needsLocale(singletonList(MessageParser.VarType.NUMBER)));
        assertFalse(MessageParser.needsLocale(singletonList(MessageParser.VarType.STRING)));
        assertFalse(MessageParser.needsLocale(emptyList()));
    }

    @Test
    void detectsNeedsTimeZone() {
        assertTrue(MessageParser.needsTimeZone(singletonList(MessageParser.VarType.DATE)));
        assertFalse(MessageParser.needsTimeZone(singletonList(MessageParser.VarType.NUMBER)));
        assertFalse(MessageParser.needsTimeZone(singletonList(MessageParser.VarType.STRING)));
        assertFalse(MessageParser.needsTimeZone(emptyList()));
    }
}
