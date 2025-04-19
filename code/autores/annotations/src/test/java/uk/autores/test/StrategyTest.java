package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.Strategy;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyTest {

    @Test
    void value() {
        Strategy[] all = Strategy.values();
        Set<String> names = Stream.of(all)
                .map(Strategy::value)
                .collect(Collectors.toSet());
        assertEquals(all.length, names.size());
    }
}