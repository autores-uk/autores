package uk.autores.handling;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertSame;

class ResourceTest {

    @Test
    void implementsCharSequence() {
        Resource.ResourceOpener ro = () -> { throw new AssertionError(); };
        CharSequenceTests.check("foo.txt", new Resource(ro, "foo.txt"));
    }

    @Test
    void open() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[10]);
        Resource resource = new Resource(() -> is, "bar");
        try (InputStream in = resource.open()) {
            assertSame(is, in);
        }
    }
}