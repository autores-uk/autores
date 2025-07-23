package uk.autores.handling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    @Test
    void key() {
        String expected = "foo";
        String actual = new Config(expected, "bar").key();
        assertEquals(expected, actual);
    }

    @Test
    void value() {
        String expected = "foo";
        String actual = new Config("bar", expected).value();
        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
        String actual = new Config("foo", "bar").toString();
        assertEquals("foo=bar", actual);
    }
}