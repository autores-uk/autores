// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.ResourceFiles;
import uk.autores.Strategy;
import uk.autores.handling.ConfigDef;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * "strategy": how to consume resources.
 * <ul>
 *     <li>"inline": embed in class files</li>
 *     <li>"const": embed in class file constant pool</li>
 *     <li>"lazy": load resources using {@link ClassLoader}</li>
 *     <li>"auto": use some heuristic to decide loading strategy</li>
 * </ul>
 */
public final class CfgStrategy {

    /** Key */
    public static final String STRATEGY = "strategy";

    /** Value */
    public static final String AUTO = Strategy.AUTO.value();
    /** Value */
    public static final String INLINE = Strategy.INLINE.value();
    /** Value */
    public static final String LAZY = Strategy.LAZY.value();
    /** Value */
    public static final String CONST = Strategy.CONST.value();

    private static final String REGEX = regex();

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(STRATEGY, s -> s.matches(REGEX));

    private CfgStrategy() {}

    private static String regex() {
        return Stream.of(Strategy.values())
                .map(Strategy::value)
                .collect(Collectors.joining("|"));
    }
}
