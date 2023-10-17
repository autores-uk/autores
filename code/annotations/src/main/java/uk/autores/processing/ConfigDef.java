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
    private final String description;

    /**
     * @param key the config key
     * @param validator must return true if the value is valid or false otherwise
     * @param description brief description of the config option
     */
    public ConfigDef(String key,
                     Predicate<String> validator,
                     String description) {
        this.key = requireNonNull(key, "name");
        this.validator = requireNonNull(validator, "validator");
        this.description = requireNonNull(description, "description");
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

    /**
     * @return brief one line description
     */
    public String description() {
        return description;
    }
}
