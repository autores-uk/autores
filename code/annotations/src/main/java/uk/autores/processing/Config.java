package uk.autores.processing;

import uk.autores.ConfigDefs;

import java.util.Objects;

/**
 * Config derived from {@link uk.autores.ClasspathResource#config()}.
 * @see ConfigDefs
 */
public final class Config {

    private final String key;
    private final String value;

    /**
     * @param key name
     * @param value configuration value
     */
    public Config(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * @return configuration key name
     * @see uk.autores.ClasspathResource.Cfg#key()
     */
    public String key() {
        return key;
    }

    /**
     * @return configuration value
     * @see uk.autores.ClasspathResource.Cfg#value()
     */
    public String value() {
        return value;
    }
}