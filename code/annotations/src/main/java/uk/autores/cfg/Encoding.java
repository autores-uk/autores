package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.processing.ConfigDef;

import java.nio.charset.Charset;

/**
 * "encoding": encoding of consumed text files.
 * @see Charset#availableCharsets()
 */
public final class Encoding {

    /** Key */
    public static final String ENCODING = "encoding";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(ENCODING,
            Charset.availableCharsets()::containsKey,
            "The canonical name of a supported encoding: " + Charset.availableCharsets().keySet());

    private Encoding() {}
}
