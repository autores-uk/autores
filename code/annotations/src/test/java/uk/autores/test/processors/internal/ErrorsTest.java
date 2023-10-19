package uk.autores.test.processors.internal;

import org.junit.jupiter.api.Test;
import uk.autores.processors.internal.Errors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorsTest {

    @Test
    void resourceErrorMessage() {
        class ClientCodeException extends RuntimeException {
            ClientCodeException() {
                super(new NullPointerException());
            }
        }

        assertNotNull(Errors.resourceErrorMessage(new RuntimeException(), "Foo.txt", "a.b"));
        assertNotNull(Errors.resourceErrorMessage(new ClientCodeException(), "Foo.txt", "a.b"));
    }

}