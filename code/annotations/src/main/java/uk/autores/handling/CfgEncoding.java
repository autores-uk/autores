// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;

import java.nio.charset.Charset;

/**
 * "encoding": encoding of consumed text files.
 * Values are validated against the keys in {@link Charset#availableCharsets()}.
 */
public final class CfgEncoding {

    /** Key */
    public static final String ENCODING = "encoding";

    /**
     * Config definition.
     * @see ConfigDef
     * @see ResourceFiles#config()
     */
    public static final ConfigDef DEF = new ConfigDef(ENCODING, Charset.availableCharsets()::containsKey);

    private CfgEncoding() {}
}
