package uk.autores;

import uk.autores.processing.ConfigDef;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class ConfigDefs {

    private ConfigDefs() {}

    static Set<ConfigDef> set(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
