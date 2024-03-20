package uk.autores;

import uk.autores.handling.CfgStrategy;

/**
 * Code generation choices.
 * Usage is {@link uk.autores.handling.Handler} specific.
 */
public enum Strategy {
    AUTO(CfgStrategy.AUTO), INLINE(CfgStrategy.INLINE), ENCODE(CfgStrategy.ENCODE), LAZY(CfgStrategy.LAZY);
    private final String value;

    Strategy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
