package uk.autores.handling;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

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
        switch (original.size()) {
            case 0:
                return emptyList();
            case 1:
                return singletonList(original.get(0));
            case 2:
                return new Pair<>(original);
            default:
                return new Unmodifiable<>(original);
        }
    }

    private static final class Unmodifiable<T> extends AbstractList<T> implements RandomAccess {
        private final Object[] contents;

        Unmodifiable(List<? extends T> original) {
            contents = original.toArray(new Object[0]);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get(int index) {
            return (T) contents[index];
        }

        @Override
        public int size() {
            return contents.length;
        }
    }

    private static final class Pair<T> extends AbstractList<T> implements RandomAccess {

        private final T first;
        private final T second;

        Pair(List<? extends T> original) {
            first = original.get(0);
            second = original.get(1);
        }

        @Override
        public T get(int index) {
            if (index == 0) {
                return first;
            }
            if (index == 1) {
                return second;
            }
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }

        @Override
        public int size() {
            return 2;
        }
    }
}
