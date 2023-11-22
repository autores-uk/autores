// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.ConfigDef;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class Sets {

    private Sets() {}

    static Set<ConfigDef> of(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
