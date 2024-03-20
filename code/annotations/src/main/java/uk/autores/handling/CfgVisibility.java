// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

/** "visibility": set to "public" to generate public instead of package visible artefacts. */
public final class CfgVisibility {

    /** Key */
    public static final String VISIBILITY = "visibility";

    /** Value */
    public static final String PUBLIC = "public";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF= new ConfigDef(VISIBILITY, PUBLIC::equals);

    private CfgVisibility() {}
}
