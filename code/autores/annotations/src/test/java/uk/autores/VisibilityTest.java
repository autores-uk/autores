package uk.autores;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisibilityTest {

    @Test
    void token() {
        Visibility[] all = Visibility.values();
        Set<String> names = Stream.of(all)
                .map(Visibility::token)
                .collect(Collectors.toSet());
        assertEquals(all.length, names.size());
    }
}