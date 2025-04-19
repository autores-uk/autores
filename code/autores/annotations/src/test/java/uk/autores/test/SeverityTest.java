package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.Severity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeverityTest {

    @Test
    void value() {
        Severity[] all = Severity.values();
        Set<String> names = Stream.of(all)
                .map(Severity::value)
                .collect(Collectors.toSet());
        assertEquals(all.length, names.size());
    }
}