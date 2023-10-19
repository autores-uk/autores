package uk.autores.cfg;

import uk.autores.ResourceFiles;
import uk.autores.processing.ConfigDef;

/**
 * "strategy": how to consume resources.
 * <ul>
 *     <li>"inline": embed in class files</li>
 *     <li>"lazy": load resources using {@link ClassLoader}</li>
 *     <li>"auto": use some heuristic to decide loading strategy</li>
 * </ul>
 * "auto" is the default strategy.
 */
public final class Strategy {

    /** Key */
    public static final String STRATEGY = "strategy";

    /** Value */
    public static final String AUTO = "auto";
    /** Value */
    public static final String INLINE = "inline";
    /** Value */
    public static final String LAZY = "lazy";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(STRATEGY, s -> s.matches("auto|inline|lazy"));

    private Strategy() {}
}
