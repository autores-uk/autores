package uk.autores;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * {@link ConfigDef} instances used by the provided {@link Handler} implementations.
 * Each {@link Handler} supports a different set of config.
 * This type is public as documentation aide.
 */
public final class ConfigDefs {

    private ConfigDefs() {}

    /** <h3>"visibility"</h3> Set to "public" to generated public instead of package visible types. Optional. */
    public static final ConfigDef VISIBILITY = new ConfigDef("visibility",
            false,
            false,
            "public"::equals,
            "Generated class visibility. Valid value: \"public\"");

    /** <h3>"encoding"</h3> Encoding of consumed text files. Optional. */
    public static final ConfigDef ENCODING = new ConfigDef("encoding",
            false,
            false,
            Charset.availableCharsets()::containsKey,
            "The canonical name of a supported encoding: " + Charset.availableCharsets().keySet());

    /**
     * <h3>"localize"</h3> Whether to search for localized resources. "true" (default) or "false". Optional.
     */
    public static final ConfigDef LOCALIZE = new ConfigDef("localize",
            false,
            false,
            s -> s.matches("true|false"),
            "Enables searching for localized resources. Valid values: \"true\"; \"false\". Default: \"true\".");

    /**
     * <h3>"missing-key"</h3> How to report missing keys in localized {@link Properties} resources.
     * "error" (default), "warn", or "ignore".
     * Optional.
     */
    public static final ConfigDef MISSING_KEY = new ConfigDef("missing-key",
            false,
            false,
            s -> s.matches("error|warn|ignore"),
            "Action when a base key is missing from localized file. Valid values: \"error\"; \"warn\"; \"ignore\". Default: \"error\".");

    /**
     * <h3>"format"</h3>
     * Whether to generate format methods. "true" (default) or "false". Optional.
     * @see java.text.MessageFormat
     */
    public static final ConfigDef FORMAT = new ConfigDef("format",
            false,
            false,
            s -> s.matches("true|false"),
            "Enables formatting. Valid values: \"true\"; \"false\". Default: \"true\".");

    static Set<ConfigDef> set(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
