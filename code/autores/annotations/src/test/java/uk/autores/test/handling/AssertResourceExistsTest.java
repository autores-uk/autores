package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.AssertResourceExists;

class AssertResourceExistsTest {

    @Test
    void handle() {
        new AssertResourceExists().handle(null);
    }
}