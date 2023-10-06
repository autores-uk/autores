package uk.autores.messages.test;

import org.junit.jupiter.api.Test;
import uk.autores.messages.PrintProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrintPropertiesTest {

    @Test
    void main() {
        PrintProperties.main();
    }

    @Test
    void shouldFormatPlanetEventString() {
        String expected = "At 12:00:00 AM on Jan 1, 1970, there was an attack on planet 4.";
        String actual = invoke(PrintProperties::printPlanetEvent).get(0);
        assertEquals(expected, actual);
    }

    private List<String> invoke(Consumer<Consumer<String>> target) {
        List<String> results = new ArrayList<>();
        target.accept(results::add);
        return results;
    }
}
