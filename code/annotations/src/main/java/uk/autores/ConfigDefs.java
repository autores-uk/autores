package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Defines and documents the configuration used by the provided {@link Handler}s.
 * Each {@link Handler} supports a different set of config.
 * This type is public as documentation aide.
 * @see ConfigDef
 */
public final class ConfigDefs {

    private ConfigDefs() {}

    /** "visibility": set to "public" to generated public instead of package visible types. Optional. */
    public static final ConfigDef VISIBILITY = new ConfigDef("visibility",
            false,
            false,
            "public"::equals,
            "Generated class visibility. Valid value: \"public\"");

    /** "encoding": Encoding of consumed text files. Optional. */
    public static final ConfigDef ENCODING = new ConfigDef("encoding",
            false,
            false,
            Charset.availableCharsets()::containsKey,
            "The canonical name of a supported encoding: " + Charset.availableCharsets().keySet());

    /**
     * "localize": whether to search for localized resources. "true" (default) or "false". Optional.
     */
    public static final ConfigDef LOCALIZE = new ConfigDef("localize",
            false,
            false,
            s -> s.matches("true|false"),
            "Enables searching for localized resources. Valid values: \"true\"; \"false\". Default: \"true\".");

    /**
     * "missing-key": how to report missing keys in localized {@link Properties} resources.
     * "error" (default), "warn", or "ignore".
     * Optional.
     */
    public static final ConfigDef MISSING_KEY = new ConfigDef("missing-key",
            false,
            false,
            s -> s.matches("error|warn|ignore"),
            "Action when a base key is missing from localized file. Valid values: \"error\"; \"warn\"; \"ignore\". Default: \"error\".");

    /**
     * "format": whether to generate format methods. "true" (default) or "false". Optional.
     * @see java.text.MessageFormat
     */
    public static final ConfigDef FORMAT = new ConfigDef("format",
            false,
            false,
            s -> s.matches("true|false"),
            "Enables formatting. Valid values: \"true\"; \"false\". Default: \"true\".");

    /**
     * "strategy": how to consume resources.
     * <ul>
     *     <li>"inline": embed in class files</li>
     *     <li>"strict": traditional resource loading but verify somehow;
     *     use compile time resource sizes for efficient loading</li>
     *     <li>"lax": traditional resource loading</li>
     *     <li>"auto": use some heuristic to decide loading strategy</li>
     * </ul>
     * "auto" is the default strategy. Optional.
     */
    public static final ConfigDef STRATEGY = new ConfigDef("strategy",
            false,
            false,
            s -> s.matches("auto|inline|strict|lax"),
            "Code generation strategy. Valid values: \"auto\"; \"inline\"; \"strict\"; \"lax\". Default: \"auto\"");

    static Set<ConfigDef> set(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
