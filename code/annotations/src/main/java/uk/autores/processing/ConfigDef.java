package uk.autores.processing;

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
     * @see uk.autores.ClasspathResource.Cfg#key()
     */
    private final String name;
    /**
     * When {@link uk.autores.ClasspathResource.Cfg#value()} passed to {@link Predicate#test(Object)} the value is considered correct.
     */
    private final Predicate<String> validator;
    /** Informational usage text. */
    private final String description;

    /**
     * @param name the config key
     * @param validator must return true if the value is valid or false otherwise
     * @param description brief description of the config option
     */
    public ConfigDef(String name,
                     Predicate<String> validator,
                     String description) {
        this.name = requireNonNull(name, "name");
        this.validator = requireNonNull(validator, "validator");
        this.description = requireNonNull(description, "description");
    }

    /**
     * @return config key name
     * @see uk.autores.ClasspathResource.Cfg#key()
     * @see Config#key()
     */
    public String name() {
        return name;
    }

    /**
     * @param value the value to test
     * @return true if the value is valid or false otherwise
     * @see uk.autores.ClasspathResource.Cfg#value()
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
