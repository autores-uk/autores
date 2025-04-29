package uk.autores;

/**
 * Error handling choices enum.
 */
public enum Severity {
    /** Processor is expected to fail compilation on error. */
    ERROR("error"),
    /** Processor is expected to emit message but continue. */
    WARN("warn"),
    /** Processor is expected to silently ignore problem. */
    IGNORE("ignore");

    private final String token;

    Severity(String token) {
        this.token = token;
    }

    /**
     * Underlying string value.
     *
     * @return configuration string
     */
    public String token() {
        return token;
    }
}
