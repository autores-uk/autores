package uk.autores;

import uk.autores.handling.CfgStrategy;

/**
 * <p>
 *     Code generation choices enum.
 *     Usage is {@link uk.autores.handling.Handler} specific.
 * </p>
 * <p>
 *     The class file format can encapsulate several hundreds of megabytes of data but
 *     this can negatively affect compile time and memory usage.
 * </p>
 */
public enum Strategy {
    /** Processor is expected to use compile-time heuristics to choose strategy. */
    AUTO(CfgStrategy.AUTO),
    /**
     * Generated code embeds data in class.
     * This generally means embedding data in class files as
     * <a href="https://docs.oracle.com/javase/specs/index.html">bytecode</a>.
     */
    INLINE(CfgStrategy.INLINE),
    /**
     * Generated code embeds data in class constant pool.
     * This generally means encoding data in class files as one or more {@link String} constants.
     */
    CONST(CfgStrategy.CONST),
    /**
     * Generated code loads data at runtime.
     * This generally means using mechanisms like {@link Class#getResourceAsStream(String)}.
     */
    LAZY(CfgStrategy.LAZY);

    private final String value;

    Strategy(String value) {
        this.value = value;
    }

    /**
     * Underlying {@link CfgStrategy} string value.
     *
     * @return configuration string
     */
    public String value() {
        return value;
    }
}
