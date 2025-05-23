package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.Visibility;

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