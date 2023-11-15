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
