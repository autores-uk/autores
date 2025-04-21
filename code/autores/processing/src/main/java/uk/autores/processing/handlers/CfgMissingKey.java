// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.ResourceFiles;
import uk.autores.Severity;
import uk.autores.handling.ConfigDef;

import java.util.Properties;

/**
 * "missing-key": how to report missing keys in localized {@link Properties} resources.
 * "error", "warn", or "ignore".
 */
public final class CfgMissingKey {

    /** Key */
    public static final String MISSING_KEY = "missing-key";

    /** Value */
    public static final String ERROR = Severity.ERROR.value();
    /** Value */
    public static final String WARN = Severity.WARN.value();
    /** Value */
    public static final String IGNORE = Severity.IGNORE.value();

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(MISSING_KEY, s -> s.matches("error|warn|ignore"));

    private CfgMissingKey() {}
}
