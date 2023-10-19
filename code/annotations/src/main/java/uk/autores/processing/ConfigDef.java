package uk.autores.processing;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Defines the configuration supported by a {@link Handler}.
 *
 * @see Handler#config()
 */
public final class ConfigDef {

    private final String key;
    private final Predicate<String> validator;

    /**
     * @param key the config key
     * @param validator must return true if the value is valid or false otherwise
     */
    public ConfigDef(String key, Predicate<String> validator) {
        this.key = requireNonNull(key, "name");
        this.validator = requireNonNull(validator, "validator");
    }

    /**
     * @return config key name
     * @see uk.autores.ResourceFiles.Cfg#key()
     * @see Config#key()
     */
    public String key() {
        return key;
    }

    /**
     * @param value the value to test
     * @return true if the value is valid or false otherwise
     * @see uk.autores.ResourceFiles.Cfg#value()
     * @see Config#value()
     */
    public boolean isValid(String value) {
        return validator.test(value);
    }
}
