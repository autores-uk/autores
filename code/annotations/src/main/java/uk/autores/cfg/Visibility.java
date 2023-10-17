package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.processing.ConfigDef;

/** "visibility": set to "public" to generated public instead of package visible types. */
public final class Visibility {

    /** Key */
    public static final String VISIBILITY = "visibility";

    /** Value */
    public static final String PUBLIC = "public";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF= new ConfigDef(VISIBILITY,
            PUBLIC::equals,
            "Generated class visibility. Valid value: \"public\"");

    private Visibility() {}
}