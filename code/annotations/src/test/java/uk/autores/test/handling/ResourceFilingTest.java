package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Pkg;
import uk.autores.handling.ResourceFiling;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceFilingTest {
    
    private final Pkg pkg = new Pkg("foo.bar");
    
    @Test
    void returnsEmptyForAbsolute() {
        String actual = ResourceFiling.pkg(pkg, "/foo/bar/baz.txt").toString();
        assertEquals("", actual);
    }

    @Test
    void returnsPackageForRelative() {
        String actual = ResourceFiling.pkg(pkg, "foo.txt").toString();
        assertEquals(pkg.toString(), actual);
    }

    @Test
    void relativeNameStripsForwardSlash() {
        String actual = ResourceFiling.relativeName("/foo/bar/baz.txt").toString();
        assertEquals("foo/bar/baz.txt", actual);
    }

    @Test
    void relativeNameIsRelativeName() {
        String actual = ResourceFiling.relativeName("foo/bar/baz.txt").toString();
        assertEquals("foo/bar/baz.txt", actual);
    }
}