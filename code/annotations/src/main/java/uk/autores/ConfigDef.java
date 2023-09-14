package uk.autores;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Defines the configuration supported by a {@link Handler}.
 *
 * @see Handler#config()
 */
public final class ConfigDef {

    /**
     * Key name.
     * @see ClasspathResource.Cfg#key()
     */
    private final String name;
    private final boolean required;
    private final boolean repeatable;
    /**
     * When {@link ClasspathResource.Cfg#value()} passed to {@link Predicate#test(Object)} the value is considered correct.
     */
    private final Predicate<String> validator;
    /** Informational usage text. */
    private final String description;

    /**
     * @param name the config key
     * @param required if true validation fails if not present
     * @param repeatable if true duplicate keys do not fail validation
     * @param validator must return true if the value is valid or false otherwise
     * @param description brief description of the config option
     */
    public ConfigDef(String name,
                     boolean required,
                     boolean repeatable,
                     Predicate<String> validator,
                     String description) {
        this.name = requireNonNull(name, "name");
        this.required = required;
        this.repeatable = repeatable;
        this.validator = requireNonNull(validator, "validator");
        this.description = requireNonNull(description, "description");
    }

    /**
     * @return config key name
     * @see ClasspathResource.Cfg#key()
     * @see Config#key()
     */
    public String name() {
        return name;
    }

    /**
     * @return true if this config must be present
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @return true if this config can be used multiple times
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * @param value the value to test
     * @return true if the value is valid or false otherwise
     * @see ClasspathResource.Cfg#value()
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
