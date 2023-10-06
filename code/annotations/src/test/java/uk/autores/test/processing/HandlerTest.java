package uk.autores.test.processing;

import org.junit.jupiter.api.Test;
import uk.autores.processing.Handler;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HandlerTest {

    private final Handler test = context -> {};

    @Test
    void config() {
        assertTrue(test.config().isEmpty());
    }

    @Test
    void validateConfig() {
        assertTrue(test.validateConfig());
    }
}
