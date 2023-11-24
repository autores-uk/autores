// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Context;
import uk.autores.handling.Namer;
import uk.autores.handling.Pkg;
import uk.autores.test.env.TestElement;
import uk.autores.test.env.TestProcessingEnvironment;
import uk.autores.test.testing.Proxies;

import javax.tools.StandardLocation;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaWriterTest {

    private final String NL = System.lineSeparator();

    @Test
    void canGenerateClass() throws IOException {
        Pkg pkg = Pkg.named("foo.bar");
        String actual = test(ctxt(pkg), jw -> {});
        // verify
        String expected = "// GENERATED CODE: uk.autores.test.JavaWriterTest" + NL;
        expected += "package foo.bar;" + NL + NL;
        expected += "final class Foo {" + NL;
        expected += NL;
        expected += "  private Foo() {}" + NL;
        expected += "}" + NL;
        assertEquals(expected, actual);
    }

    @Test
    void nl() throws IOException {
        String actual = test(ctxt(), JW::nl);
        // verify
        String expected = "// GENERATED CODE: uk.autores.test.JavaWriterTest" + NL;
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
        String expected = "// GENERATED CODE: uk.autores.test.JavaWriterTest" + NL;
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
        String expected = "// GENERATED CODE: uk.autores.test.JavaWriterTest" + NL;
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
        try (JW jw = instance(this, ctxt, sw, "Foo", "")) {
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
                .setConfig(Collections.emptyList())
                .setLocation(StandardLocation.CLASS_PATH)
                .setNamer(new Namer())
                .setPkg(pkg)
                .setResources(Collections.emptyList())
                .build();
        return context;
    }

    private JW instance(Object generator, Context ctxt, Writer writer, String className, String comment) {
        Class<?>[] types = {
                Object.class,
                Context.class,
                Writer.class,
                String.class,
                CharSequence.class,
        };
        Object[] args = {
                generator,
                ctxt,
                writer,
                className,
                comment,
        };
        return Proxies.instance(JW.class, "uk.autores.JavaWriter", types, args);
    }

    private interface JW extends Closeable {
        void flush() throws IOException;
        JW indent() throws IOException;
        JW append(char c) throws IOException;
        JW append(CharSequence cs) throws IOException;
        JW staticFinal(String s1, String s2) throws IOException;
        JW write(char[] arr, int off, int len) throws IOException;
        JW nl() throws IOException;
    }

    @FunctionalInterface
    private interface Invoker {
        void invoke(JW jw) throws IOException;
    }
}
