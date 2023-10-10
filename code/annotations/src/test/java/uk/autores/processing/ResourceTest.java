package uk.autores.processing;

import org.junit.jupiter.api.Test;
import uk.autores.env.TestFileObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {

    @Test
    void open() throws IOException {
        byte[] bytes = "foo".getBytes(StandardCharsets.UTF_8);

        TestFileObject data = new TestFileObject(true);
        data.data.out().write(bytes);

        Resource resource = new Resource(data, "bar");
        try (InputStream in = resource.open()) {
            for (byte b : bytes) {
                assertEquals(b, in.read());
            }
        }
    }

    @Test
    void path() {
        TestFileObject data = new TestFileObject(true);

        assertEquals("foo.txt", new Resource(data, "foo.txt").path());
        assertEquals("/bar/foo.txt", new Resource(data, "/bar/foo.txt").path());
        assertEquals("bar/foo.txt", new Resource(data, "/bar/foo.txt").filerPath());
    }

    @Test
    void implementsEqualsHashcodeEtc() {
        TestFileObject data = new TestFileObject(true);
        Resource a1 = new Resource(data, "a.txt");
        Resource a2 = new Resource(data, "a.txt");
        Resource b = new Resource(data, "b.txt");

        // equals
        assertTrue(a1.equals(a1));
        assertEquals(a1, a2);
        assertNotEquals(a1, b);
        assertFalse(a1.equals(null));
        assertFalse(a1.equals(new Object()));
        // hashCode
        assertEquals(a1.path().hashCode(), a1.hashCode());
        // comparable
        assertEquals(a1.path().compareTo(a2.path()), a1.compareTo(a2));
        assertEquals(a1.path().compareTo(b.path()), a1.compareTo(b));
        assertEquals(b.path().compareTo(a1.path()), b.compareTo(a1));
    }
}