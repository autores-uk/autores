package uk.autores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateTrackerTest {

    @Test
    void get() {
        StateTracker<Foo> st = StateTracker.of(Foo.class);
        boolean initial = st.getOrSet(Foo.A, () -> true);
        boolean actual = st.getOrSet(Foo.A, () -> false);
        assertTrue(initial);
        assertTrue(actual);
    }

    private enum Foo {
        A
    }
}