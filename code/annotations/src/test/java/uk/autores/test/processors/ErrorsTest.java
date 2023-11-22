// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.processors;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorsTest {

    private final EProxy Errors = Proxies.utility(EProxy.class, "uk.autores.processors.Errors");

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

    private interface EProxy {
        String resourceErrorMessage(Exception e, CharSequence resource, CharSequence pkg);
    }
}