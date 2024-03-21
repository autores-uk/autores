// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

/**
 * "localize": whether to search for localized resources. "true" or "false".
 */
public final class CfgLocalize {

    /** Key */
    public static final String LOCALIZE = "localize";

    /** Value */
    public static final String TRUE = "true";
    /** Value */
    public static final String FALSE = "false";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(LOCALIZE, s -> s.matches("true|false"));

    private CfgLocalize() {}
}
