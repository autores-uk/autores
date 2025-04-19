// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import uk.autores.handling.ConfigDef;
import uk.autores.naming.Namer;

/**
 * "name": generated class name.
 */
public final class CfgName {

    private CfgName() {}

    /** Key */
    public static final String NAME = "name";

    /** Name configuration definition */
    public static final ConfigDef DEF = new ConfigDef(NAME, Namer::isIdentifier);
}
