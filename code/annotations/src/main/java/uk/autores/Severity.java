package uk.autores;

import uk.autores.handling.CfgMissingKey;

/**
 * Error handling choices enum.
 * Usage is {@link uk.autores.handling.Handler} specific.
 */
public enum Severity {
    /** Processor is expected to fail compilation on error. */
    ERROR(CfgMissingKey.ERROR),
    /** Processor is expected to emit message but continue. */
    WARN(CfgMissingKey.WARN),
    /** Processor is expected to silently ignore problem. */
    IGNORE(CfgMissingKey.IGNORE);

    private final String value;

    Severity(String value) {
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
