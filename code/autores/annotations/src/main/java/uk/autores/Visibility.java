package uk.autores;

/**
 * Indicates visibility of generated code.
 */
public enum Visibility {
    /** Package private. The default visibility. */
    PACKAGE(""),
    /** Public. Elements are declared with the <code>public</code> keyword. */
    PUBLIC("public");

    private final String id;

    Visibility(String id) {
        this.id = id;
    }

    /**
     * Underlying string value.
     *
     * @return configuration string
     */
    public String token() {
        return id;
    }
}
