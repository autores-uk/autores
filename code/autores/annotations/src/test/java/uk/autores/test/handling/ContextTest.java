package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.*;
import uk.autores.naming.Namer;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    private final ProcessingEnvironment env = fake(ProcessingEnvironment.class, this::unsupported);
    private final Element annotated = fake(Element.class, this::unsupported);
    private final Config cfg = new Config("foo", "bar");
    private final Namer namer = new Namer();
    private final Pkg pkg = Pkg.named("foo");
    private final JavaFileManager.Location location = StandardLocation.CLASS_PATH;
    private final Resource resource = new Resource(() -> {
        throw new UnsupportedOperationException();
    }, "somefile.txt");

    @Test
    void builder() {
        Context.Builder b = Context.builder();
        assertNotNull(b);
    }

    private Context context() {
        return Context.builder()
                .setAnnotated(annotated)
                .setConfig(singletonList(cfg))
                .setNamer(namer)
                .setEnv(env)
                .setLocation(singletonList(location))
                .setPkg(pkg)
                .setResources(singletonList(resource))
                .build();
    }

    @Test
    void rebuild() {
        Pkg expected = Pkg.named("bar");
        Pkg actual = context().rebuild()
                .setPkg(expected)
                .build()
                .pkg();
        assertEquals(expected, actual);
    }

    @Test
    void option() {
        {
            ConfigDef foo = new ConfigDef("foo", s -> true);
            String actual = context().option(foo)
                    .orElseThrow(AssertionError::new);
            assertEquals(cfg.value(), actual);
        }
        {
            ConfigDef unknown = new ConfigDef("unknown", s -> true);
            boolean present = context().option(unknown)
                    .isPresent();
            assertFalse(present);
        }
    }

    @Test
    void printError() {
        AtomicReference<Object[]> ref = new AtomicReference<>();
        Messager msr = fake(Messager.class, (p, m, a) -> {
            assertEquals("printMessage", m.getName());
            ref.set(a);
            return null;
        });
        ProcessingEnvironment pe = fake(ProcessingEnvironment.class, (p, m, a) -> {
            assertEquals("getMessager", m.getName());
            return msr;
        });

        String expected = "yikes";
        Context c = context().rebuild()
                .setEnv(pe)
                .build();
        c.printError(expected);

        assertEquals(3, ref.get().length);
        assertEquals(Diagnostic.Kind.ERROR, ref.get()[0]);
        assertEquals(expected, ref.get()[1]);
        assertSame(annotated, ref.get()[2]);
    }

    @Test
    void env() {
        assertSame(env, context().env());
    }

    @Test
    void locations() {
        List<JavaFileManager.Location> l = context().locations();
        assertEquals(1, l.size());
        assertSame(location, l.get(0));
    }

    @Test
    void pkg() {
        assertEquals(pkg, context().pkg());
    }

    @Test
    void annotated() {
        assertSame(annotated, context().annotated());
    }

    @Test
    void resources() {
        List<Resource> l = context().resources();
        assertEquals(1, l.size());
        assertSame(resource, l.get(0));
    }

    @Test
    void config() {
        List<Config> l = context().config();
        assertEquals(1, l.size());
        assertSame(cfg, l.get(0));
    }

    @Test
    void namer() {
        assertSame(namer, context().namer());
    }

    @Test
    void listsAreDefensiveCopies() {
        List<Config> cfgs = Arrays.asList(cfg, new Config("a", "b"));
        List<Config> actual = context().rebuild()
                .setConfig(cfgs)
                .build()
                .config();
        assertNotSame(cfgs, actual);
    }

    @SuppressWarnings("unchecked")
    private static <T> T fake(Class<T> c, InvocationHandler ih) {
        ClassLoader cl = ContextTest.class.getClassLoader();
        Class<?>[] i = {c};
        return (T) Proxy.newProxyInstance(cl, i, ih);
    }

    private Object unsupported(Object p, Method m, Object[] a) {
        throw new UnsupportedOperationException();
    }
}