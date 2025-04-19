package uk.autores;

/**
 * <p>
 *     Code generation choices enum.
 *     Usage is specific to {@link ByteArrays} and {@link Texts}.
 * </p>
 * <p>
 *     The class file format can encapsulate several hundreds of megabytes of data but
 *     this can negatively affect compile times and runtime memory usage.
 *     Use with caution.
 * </p>
 */
public enum Strategy {
    /** Processor is expected to use compile-time heuristics to choose strategy. */
    AUTO("auto"),
    /**
     * Generated code embeds data in class.
     * This generally means embedding data in class files as
     * <a href="https://docs.oracle.com/javase/specs/index.html">bytecode</a>.
     */
    INLINE("inline"),
    /**
     * Generated code embeds data in class constant pool.
     * This generally means encoding data in class files as one or more {@link String} constants.
     */
    CONST("const"),
    /**
     * Generated code loads data at runtime.
     * This generally means using mechanisms like {@link Class#getResourceAsStream(String)}.
     */
    LAZY("lazy");

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
