package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.handling.ConfigDef;

/** "visibility": set to "public" to generate public instead of package visible artefacts. */
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
    public static final ConfigDef DEF= new ConfigDef(VISIBILITY, PUBLIC::equals);

    private Visibility() {}
}
