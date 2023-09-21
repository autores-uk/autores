package uk.autores;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringLiteralsTest {

    @Test
    void escapesStrings() throws IOException {
        StringWriter sw = new StringWriter();
        StringLiterals.write("\t \" \r \n \\ \f", sw);
        assertEquals("\\t \\\" \\r \\n \\\\ \\f", sw.toString());
    }
}
