package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.processing.ConfigDef;

public final class Format {

    /** Key */
    public static final String FORMAT = "format";

    /** Value */
    public static final String TRUE = "true";
    /** Value */
    public static final String FALSE = "false";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(FORMAT,
            s -> s.matches("true|false"),
            "Enables formatting. Valid values: \"true\"; \"false\".");

    private Format() {}
}
