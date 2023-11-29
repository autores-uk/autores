// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class MessageParserTest {

    private static final String STRING = String.class.getName();
    private static final String NUMBER = Number.class.getName();
    private static final String DATE = ZonedDateTime.class.getName();

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
        assertEquals(0, parser.firstDateIndex(singletonList(DATE)));
        assertEquals(-1, parser.firstDateIndex(singletonList(NUMBER)));
        assertEquals(-1, parser.firstDateIndex(singletonList(STRING)));
        assertEquals(-1, parser.firstDateIndex(emptyList()));
        assertEquals(1, parser.firstDateIndex(Arrays.asList(STRING, DATE, DATE, NUMBER)));
    }

    @Test
    void detectsVariableReuse() {
        assertTrue(parser.variableReuse("{0} {0}"));
        assertFalse(parser.variableReuse("{0}"));
        assertFalse(parser.variableReuse(""));
    }

    private interface MP {
        List<String> parse(String s);
        int firstDateIndex(List<String> vars);
        boolean needsLocale(List<String> vars);
        boolean variableReuse(String pattern);
    }
}
