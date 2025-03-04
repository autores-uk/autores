package uk.autores.handling;

import org.junit.jupiter.api.Test;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.test.testing.env.TestProcessingEnvironment;

import javax.lang.model.element.Element;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void rebuild() {
        ClassLoader cl = ContextTest.class.getClassLoader();
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
                .build();

        Context actual = ctxt.rebuild().build();
        assertNotNull(actual);
    }
}