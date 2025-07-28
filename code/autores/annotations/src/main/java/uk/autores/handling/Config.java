// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;

import static java.util.Objects.requireNonNull;

/**
 * Config item derived from {@link ResourceFiles#config()}.
 */
public final class Config {

    private final String key;
    private final String value;

    /**
     * Constructor with mandatory values.
     *
     * @param key name
     * @param value configuration value
     */
    public Config(String key, String value) {
        this.key = requireNonNull(key);
        this.value = requireNonNull(value);
    }

    /**
     * Key.
     *
     * @return configuration key name
     * @see ResourceFiles.Cfg#key()
     */
    public String key() {
        return key;
    }

    /**
     * Value.
     *
     * @return configuration value
     * @see ResourceFiles.Cfg#value()
     */
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
