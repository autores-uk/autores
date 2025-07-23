// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Context;
import uk.autores.handling.Pkg;
import uk.autores.naming.Namer;
import uk.autores.processing.testing.env.TestElement;
import uk.autores.processing.testing.env.TestProcessingEnvironment;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaWriterTest {

    private final String NL = System.lineSeparator();

    @Test
    void canGenerateClass() throws IOException {
        Pkg pkg = Pkg.named("foo.bar");
        String actual = test(ctxt(pkg), jw -> {});
        // verify
        String expected = "// GENERATED CODE: uk.autores.processing.handlers.JavaWriterTest" + NL;
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
        String expected = "// GENERATED CODE: uk.autores.processing.handlers.JavaWriterTest" + NL;
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
        String expected = "// GENERATED CODE: uk.autores.processing.handlers.JavaWriterTest" + NL;
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
        String expected = "// GENERATED CODE: uk.autores.processing.handlers.JavaWriterTest" + NL;
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
        try (JavaWriter jw = instance(this, ctxt, sw, "Foo", "")) {
            invoker.invoke(jw);
        }
        return sw.toString();
    }

    private Context ctxt() {
        return ctxt(Pkg.named(""));
    }

    private Context ctxt(Pkg pkg) {
        TestProcessingEnvironment env = new TestProcessingEnvironment();

        Context context = Context.builder()
                .setAnnotated(TestElement.INSTANCE)
                .setEnv(env)
                .setConfig(emptyList())
                .setLocation(singletonList(StandardLocation.CLASS_PATH))
                .setNamer(new Namer())
                .setPkg(pkg)
                .setResources(emptyList())
                .build();
        return context;
    }

    private JavaWriter instance(Object generator, Context ctxt, Writer writer, String className, String comment) throws IOException {
        return new JavaWriter(generator,
                        ctxt,
                        writer,
                        className,
                        comment);
    }

    @FunctionalInterface
    private interface Invoker {
        void invoke(JavaWriter jw) throws IOException;
    }
}
