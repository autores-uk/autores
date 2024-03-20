// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.cfg;

import uk.autores.handling.ConfigDef;
import uk.autores.naming.Namer;

/**
 * "name": generated class name.
 */
public final class Name {

    private Name() {}

    /** Key */
    public static final String NAME = "name";

    public static final ConfigDef DEF = new ConfigDef(NAME, Namer::isIdentifier);
}
