package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.format.FormatExpression;
import uk.autores.handling.Context;
import uk.autores.handling.Pkg;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.processing.test.testing.env.TestProcessingEnvironment;

import javax.lang.model.element.Element;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

class GenerateMessagesTest {

    @Test
    void exerciseListCode() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Need JDK22 to use java.text.ListFormat so just exercise code for now
        FormatExpression expression = FormatExpression.parse("{0,list} {0,list,or} {0,list,unit}");
        StringWriter buf = new StringWriter();
        try (Closeable jw = JavaWriter(buf)) {
            write(jw, Locale.US, expression, false);
        } catch (IOException e) {
            fail(e);
        }

        assertFalse(buf.toString().isEmpty());
    }

    private static Closeable JavaWriter(Writer w) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassLoader cl = GenerateMessagesTest.class.getClassLoader();
        Class<?>[] c = {Element.class};
        InvocationHandler ih = (Object proxy, Method method, Object[] args) -> {
            throw new AssertionError();
        };
        Element element = (Element) Proxy.newProxyInstance(cl, c, ih);

        Context ctxt = Context.builder()
                .setAnnotated(element)
                .setEnv(new TestProcessingEnvironment())
                .setNamer(new IdiomaticNamer())
                .setPkg(Pkg.named(""))
                .setConfig(Collections.emptyList())
                .build();

        Class<?> JavaWriter = cl.loadClass("uk.autores.processing.handlers.JavaWriter");
        Constructor<?> ctor = JavaWriter.getDeclaredConstructor(Object.class, Context.class, Writer.class, String.class, CharSequence.class);
        ctor.setAccessible(true);
        return (Closeable) ctor.newInstance("X", ctxt, w, "Foo", "ignored");
    }

    private static void write(Closeable w, Locale l, FormatExpression expression, boolean estLength) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader cl = GenerateMessagesTest.class.getClassLoader();
        Class<?> JavaWriter = cl.loadClass("uk.autores.processing.handlers.JavaWriter");
        Class<?> GenerateMessages = cl.loadClass("uk.autores.processing.handlers.GenerateMessages");
        Method write = GenerateMessages.getDeclaredMethod("write", JavaWriter, Locale.class, FormatExpression.class, Boolean.TYPE);
        write.setAccessible(true);
        write.invoke(null, w, l, expression, estLength);
    }
}