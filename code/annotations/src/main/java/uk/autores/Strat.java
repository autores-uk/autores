package uk.autores;

import uk.autores.cfg.Strategy;

/**
 * Code generation strategy.
 * Usage is {@link uk.autores.handling.Handler} specific.
 */
public enum Strat {
    AUTO(Strategy.AUTO), INLINE(Strategy.INLINE), ENCODE(Strategy.ENCODE), LAZY(Strategy.LAZY);
    private final String value;

    Strat(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
