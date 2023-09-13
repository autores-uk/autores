package uk.autores;

import java.util.Objects;

/**
 * Config derived from {@link ClasspathResource.Cfg}.
 */
public final class Config {

    private final String key;
    private final String value;

    public Config(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }
}
