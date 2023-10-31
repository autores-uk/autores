package uk.autores;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

final class StateTracker<E extends Enum<E>> {

    private Map<E, Object> cache;

    private StateTracker(Class<E> type) {
        cache = new EnumMap<>(type);
    }

    static <X extends Enum<X>> StateTracker<X> of(Class<X> type) {
        Objects.requireNonNull(type);
        return new StateTracker<>(type);
    }

    @SuppressWarnings("unchecked")
    <T> T getOrSet(E key, Supplier<T> generator) {
        return (T) cache.computeIfAbsent(key, k -> generator.get());
    }
}
