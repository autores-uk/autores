package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListsTest {

    @Test
    void canHandlePair() {
        List<String> original = Arrays.asList("one", "two");

        List<String> actual = proxy().copy(original);

        assertEquals(original, actual);
        assertNotSame(original, actual);
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> actual.get(2));
    }

    private L proxy() {
        return Proxies.utility(L.class, "uk.autores.handling.Lists");
    }

    private interface L {
        <T> List<T> copy(List<? extends T> original);
    }
}