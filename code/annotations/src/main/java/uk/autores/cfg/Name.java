package uk.autores.cfg;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Namer;

/**
 * Generated class name.
 */
public final class Name {

    private Name() {}

    /** Key */
    public static final String NAME = "name";

    public static final ConfigDef DEF = new ConfigDef(NAME, Namer::isJavaIdentifier);
}
