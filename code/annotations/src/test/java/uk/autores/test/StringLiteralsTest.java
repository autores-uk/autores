package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringLiteralsTest {

    @Test
    void escapesStrings() throws IOException {
        SL StringLiterals = Proxies.utility(SL.class, "uk.autores.StringLiterals");

        StringWriter sw = new StringWriter();
        StringLiterals.write("\t \" \r \n \\ \f", sw);
        assertEquals("\\t \\\" \\r \\n \\\\ \\f", sw.toString());
    }

    private interface SL {
        void write(CharSequence cs, Writer w) throws IOException;
    }
}
