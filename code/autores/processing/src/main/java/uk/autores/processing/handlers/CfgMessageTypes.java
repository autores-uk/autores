package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.naming.Namer;

/**
 * Message format method signature types.
 */
public final class CfgMessageTypes {
    private CfgMessageTypes() {}

    /** Key */
    public static final String NONE_TYPE = "noneType";
    /** Key */
    public static final String NUMBER_TYPE = "numberType";
    /** Key */
    public static final String DATE_TIME_TYPE = "dateTimeType";

    /** Config definition */
    public static final ConfigDef DEF_NONE = new ConfigDef(NONE_TYPE, Namer::isPackage);
    /** Config definition */
    public static final ConfigDef DEF_NUMBER = new ConfigDef(NUMBER_TYPE, Namer::isPackage);
    /** Config definition */
    public static final ConfigDef DEF_DATE_TIME = new ConfigDef(DATE_TIME_TYPE, Namer::isPackage);
}
