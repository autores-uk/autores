package uk.autores;

import uk.autores.handling.CfgStrategy;

/**
 * Code generation choices.
 * Usage is {@link uk.autores.handling.Handler} specific.
 */
public enum Strategy {
    /** Processor is expected to use compile-time heuristics to choose strategy. */
    AUTO(CfgStrategy.AUTO),
    /** Generated code embeds data in class. */
    INLINE(CfgStrategy.INLINE),
    /** Generated code embeds data in class constant pool. */
    CONST(CfgStrategy.CONST),
    /** Generated code loads data at runtime. */
    LAZY(CfgStrategy.LAZY);
    private final String value;

    Strategy(String value) {
        this.value = value;
    }

    /**
     * Underlying string value.
     *
     * @return configuration string
     */
    public String value() {
        return value;
    }
}
