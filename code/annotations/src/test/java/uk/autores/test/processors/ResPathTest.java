package uk.autores.test.processors;

import org.junit.jupiter.api.Test;
import uk.autores.internal.ResPath;
import uk.autores.processing.Pkg;
import uk.autores.test.env.TestElement;
import uk.autores.test.env.TestProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResPathTest {

    private final Element element = new TestElement();
    private final Pkg relative = new Pkg("foo", true);
    private final Pkg absolute = new Pkg("foo", false);

    @Test
    void reportResourceNameNotEmpty() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        String resource = ResPath.massage(env, element, absolute, "");
        assertNull(resource);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportAbsolutePathStartsWithSlash() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        String resource = ResPath.massage(env, element, absolute, "foo.txt");
        assertNull(resource);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void reportRelativePathDoesNotStartWithSlash() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        String resource = ResPath.massage(env, element, relative, "/foo/bar.txt");
        assertNull(resource);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void allowsRelativePath() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        String resource = ResPath.massage(env, element, relative, "foo.txt");
        assertEquals("foo.txt", resource);
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    @Test
    void allowsAbsolutePath() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        String resource = ResPath.massage(env, element, absolute, "/foo/bar.txt");
        assertEquals("foo/bar.txt", resource);
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }
}