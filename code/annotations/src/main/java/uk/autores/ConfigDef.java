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

    public String name() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public boolean isValid(String value) {
        return validator.test(value);
    }

    public String description() {
        return description;
    }
}
