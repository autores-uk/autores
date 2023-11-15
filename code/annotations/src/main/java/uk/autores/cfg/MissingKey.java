package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.handling.ConfigDef;

import java.util.Properties;

/**
 * "missing-key": how to report missing keys in localized {@link Properties} resources.
 * "error", "warn", or "ignore".
 */
public final class MissingKey {

    /** Key */
    public static final String MISSING_KEY = "missing-key";

    /** Value */
    public static final String ERROR = "error";
    /** Value */
    public static final String WARN = "warn";
    /** Value */
    public static final String IGNORE = "ignore";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(MISSING_KEY, s -> s.matches("error|warn|ignore"));

    private MissingKey() {}
}
