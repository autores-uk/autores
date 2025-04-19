// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.ResourceFiles;
import uk.autores.handling.ConfigDef;

/**
 * "format": whether to generate format methods. "true" or "false".
 */
public final class CfgFormat {

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
    public static final ConfigDef DEF = new ConfigDef(FORMAT, s -> s.matches("true|false"));

    private CfgFormat() {}
}
