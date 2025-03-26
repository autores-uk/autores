// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;

import java.util.Properties;

/**
 * "incompatible-format": how to report incompatible format strings in localized {@link Properties} resources.
 * "error", "warn", or "ignore".
 */
public final class CfgIncompatibleFormat {

    /** Key */
    public static final String INCOMPATIBLE_FORMAT = "incompatible-format";

    /** Value */
    public static final String ERROR = "error";
    /** Value */
    public static final String WARN = "warn";
    /** Value */
    public static final String IGNORE = "ignore";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(INCOMPATIBLE_FORMAT, s -> s.matches("error|warn|ignore"));

    private CfgIncompatibleFormat() {}
}
