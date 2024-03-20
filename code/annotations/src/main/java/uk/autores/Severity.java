package uk.autores;

import uk.autores.cfg.MissingKey;

/**
 * Error handling.
 */
public enum Severity {
    ERROR(MissingKey.ERROR), WARN(MissingKey.WARN), IGNORE(MissingKey.IGNORE);

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
