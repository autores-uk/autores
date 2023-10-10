package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.Context;
import uk.autores.processing.Namer;
import uk.autores.processing.Pkg;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaWriterTest {

    private final String NL = System.lineSeparator();

    @Test
    void canGenerateClass() throws IOException {
        Pkg pkg = new Pkg("foo.bar", false);
        String actual = test(ctxt(pkg), jw -> {});
        // verify
        String expected = "// GENERATED CODE: uk.autores.JavaWriterTest" + NL;
        expected += "package foo.bar;" + NL + NL;
        expected += "final class Foo {" + NL;
        expected += NL;
        expected += "  private Foo() {}" + NL;
        expected += "}" + NL;
        assertEquals(expected, actual);
    }

    @Test
    void nl() throws IOException {
        String actual = test(ctxt(), JavaWriter::nl);
        // verify
        String expected = "// GENERATED CODE: uk.autores.JavaWriterTest" + NL;
        expected += "final class Foo {" + NL;
        expected += NL;
        expected += "  private Foo() {}" + NL;
        expected += NL;
        expected += "}" + NL;
        assertEquals(expected, actual);
    }

    @Test
    void arbitrary() throws IOException {
        String actual = test(ctxt(), jw -> {
            jw.write(NL.toCharArray(), 0, NL.length());
            jw.append(' ');
        });
        // verify
        String expected = "// GENERATED CODE: uk.autores.JavaWriterTest" + NL;
        expected += "final class Foo {" + NL;
        expected += NL;
        expected += "  private Foo() {}" + NL;
        expected += NL + ' ';
        expected += "}" + NL;
        assertEquals(expected, actual);
    }

    @Test
    void staticField() throws IOException {
        String actual = test(ctxt(), jw -> {
            jw.indent().staticFinal("String", "bar").append("\"x\";").nl();
        });
        // verify
        String expected = "// GENERATED CODE: uk.autores.JavaWriterTest" + NL;
        expected += "final class Foo {" + NL;
        expected += NL;
        expected += "  private Foo() {}" + NL;
        expected += "  static final String bar = \"x\";" + NL;
        expected += "}" + NL;
        assertEquals(expected, actual);
    }

    @Test
    void closeIsIdempotent() throws IOException {
        test(ctxt(), jw -> {
            jw.flush();
            jw.close();
            jw.close();
        });
    }

    private String test(Context ctxt, Invoker invoker) throws IOException {
        StringWriter sw = new StringWriter();
        try (JavaWriter jw = new JavaWriter(this, ctxt, sw, "Foo", "")) {
            invoker.invoke(jw);
        }
        return sw.toString();
    }

    private Context ctxt() {
        return ctxt(TestPkgs.P);
    }

    private Context ctxt(Pkg pkg) {
        TestProcessingEnvironment env = new TestProcessingEnvironment();

        return new Context(env, StandardLocation.SOURCE_OUTPUT, pkg, TestElement.INSTANCE, Collections.emptySortedSet(), Collections.emptyList(), new Namer());
    }

    @FunctionalInterface
    private interface Invoker {
        void invoke(JavaWriter jw) throws IOException;
    }
}