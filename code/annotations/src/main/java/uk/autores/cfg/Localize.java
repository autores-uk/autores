package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.processing.ConfigDef;

/**
 * "localize": whether to search for localized resources. "true" or "false".
 */
public final class Localize {

    /** Key */
    public static final String LOCALIZE = "localize";

    /** Value */
    public static final String TRUE = "true";
    /** Value */
    public static final String FALSE = "false";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(LOCALIZE,
            s -> s.matches("true|false"),
            "Enables searching for localized resources. Valid values: \"true\"; \"false\".");

    private Localize() {}
}
