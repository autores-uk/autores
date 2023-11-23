package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharSequenceSetTest {

    @Test
    void contains() {
        String foo = "foo";
        String bar = "bar";
        String baz = "baz";
        String triumvirate = "triumvirate";
        String triumvirate1 = "triumvirate" + 1;
        String foobar = foo + bar;
        String foobaz = foo + baz;
        String bazbar = baz + bar;

        CSS set = instance(foo, bar, triumvirate);
        assertTrue(set.contains(foo, 0, foo.length()));
        assertTrue(set.contains(bar, 0, bar.length()));
        assertTrue(set.contains(triumvirate, 0, triumvirate.length()));

        assertFalse(set.contains(baz, 0, baz.length()));
        assertFalse(set.contains(triumvirate1, 0, triumvirate1.length()));
        assertTrue(set.contains(foobaz, 0, 3));
        assertTrue(set.contains(foobar, 3, 3));
        assertTrue(set.contains(bazbar, 3, 3));
    }

    private CSS instance(CharSequence...cs) {
        Class<?>[] types = {CharSequence[].class};
        Object[] args = {cs};
        return Proxies.instance(CSS.class, "uk.autores.handling.CharSequenceSet", types, args);
    }

    private interface CSS {
        boolean contains(CharSequence cs, int off, int len);
    }
}