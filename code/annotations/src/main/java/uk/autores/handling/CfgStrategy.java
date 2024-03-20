// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

/**
 * "strategy": how to consume resources.
 * <ul>
 *     <li>"inline": embed in class files</li>
 *     <li>"lazy": load resources using {@link ClassLoader}</li>
 *     <li>"auto": use some heuristic to decide loading strategy</li>
 * </ul>
 * "auto" is the default strategy.
 */
public final class CfgStrategy {

    /** Key */
    public static final String STRATEGY = "strategy";

    /** Value */
    public static final String AUTO = "auto";
    /** Value */
    public static final String INLINE = "inline";
    /** Value */
    public static final String LAZY = "lazy";
    /** Value */
    public static final String ENCODE = "encode";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(STRATEGY, s -> s.matches(
            AUTO + '|' + INLINE + '|' + LAZY + '|' + ENCODE
    ));

    private CfgStrategy() {}
}
