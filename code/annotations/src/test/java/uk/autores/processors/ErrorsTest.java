package uk.autores.processors;

import org.junit.jupiter.api.Test;

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