package uk.autores;

import uk.autores.handling.CfgMissingKey;

/**
 * Error handling choices.
 * Usage is {@link uk.autores.handling.Handler} specific.
 */
public enum Severity {
    ERROR(CfgMissingKey.ERROR), WARN(CfgMissingKey.WARN), IGNORE(CfgMissingKey.IGNORE);

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
