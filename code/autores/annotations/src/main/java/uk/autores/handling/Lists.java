package uk.autores.handling;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Utility type.
 */
final class Lists {
    private Lists() {}

    /**
     * @param original list to copy
     * @return unmodifiable copy
     * @param <T> list content type
     */
    static <T> List<T> copy(List<? extends T> original) {
        return original.isEmpty()
                ? emptyList()
                : List.copyOf(original);
    }
}
