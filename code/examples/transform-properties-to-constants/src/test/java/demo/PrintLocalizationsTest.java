package demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrintLocalizationsTest {

    @Test
    void main() {
        PrintLocalizations.main();
    }

    @Test
    void shouldLoadString() {
        String actual = invoke(PrintLocalizations::printLocalizations).get(0);
        assertEquals("English: Liberty, equality, fraternity.", actual);
    }

    private List<String> invoke(Consumer<Consumer<String>> target) {
        List<String> results = new ArrayList<>();
        target.accept(results::add);
        return results;
    }
}
