// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Resource;
import uk.autores.test.testing.env.TestFileObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceTest {

    @Test
    void implementsCharSequence() {
        TestFileObject data = new TestFileObject(true);
        CharSequenceTests.check("foo.txt", new Resource(data::openInputStream, "foo.txt"));
    }

    @Test
    void open() throws IOException {
        byte[] bytes = "foo".getBytes(StandardCharsets.UTF_8);

        TestFileObject data = new TestFileObject(true);
        data.data.out().write(bytes);

        Resource resource = new Resource(data::openInputStream, "bar");
        try (InputStream in = resource.open()) {
            for (byte b : bytes) {
                assertEquals(b, in.read());
            }
        }
    }
}
