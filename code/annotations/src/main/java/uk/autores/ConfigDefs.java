package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

final class ConfigDefs {

    private ConfigDefs() {}

    static Set<ConfigDef> set(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
