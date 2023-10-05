package uk.autores.test.internal;

import org.junit.jupiter.api.Test;
import uk.autores.internal.MessageParser;

import java.util.List;

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
}
