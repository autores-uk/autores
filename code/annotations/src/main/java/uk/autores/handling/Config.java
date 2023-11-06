package uk.autores.handling;

import uk.autores.ResourceFiles;

import java.util.Objects;

/**
 * Config derived from {@link ResourceFiles#config()}.
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
     * @see ResourceFiles.Cfg#key()
     */
    public String key() {
        return key;
    }

    /**
     * @return configuration value
     * @see ResourceFiles.Cfg#value()
     */
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
