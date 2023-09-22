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
 * @see ConfigDef
 */
public final class ConfigDefs {

    private ConfigDefs() {}

    /** "visibility": set to "public" to generated public instead of package visible types. */
    public static final ConfigDef VISIBILITY = new ConfigDef("visibility",
            "public"::equals,
            "Generated class visibility. Valid value: \"public\"");

    /**
     * "encoding": encoding of consumed text files.
     * @see Charset#availableCharsets()
     */
    public static final ConfigDef ENCODING = new ConfigDef("encoding",
            Charset.availableCharsets()::containsKey,
            "The canonical name of a supported encoding: " + Charset.availableCharsets().keySet());

    /**
     * "localize": whether to search for localized resources. "true" or "false".
     */
    public static final ConfigDef LOCALIZE = new ConfigDef("localize",
            s -> s.matches("true|false"),
            "Enables searching for localized resources. Valid values: \"true\"; \"false\".");

    /**
     * "missing-key": how to report missing keys in localized {@link Properties} resources.
     * "error", "warn", or "ignore".
     */
    public static final ConfigDef MISSING_KEY = new ConfigDef("missing-key",
            s -> s.matches("error|warn|ignore"),
            "Action when a base key is missing from localized file. Valid values: \"error\"; \"warn\"; \"ignore\".");

    /**
     * "format": whether to generate format methods. "true" (default) or "false".
     * @see java.text.MessageFormat
     */
    public static final ConfigDef FORMAT = new ConfigDef("format",
            s -> s.matches("true|false"),
            "Enables formatting. Valid values: \"true\"; \"false\".");

    /**
     * "strategy": how to consume resources.
     * <ul>
     *     <li>"inline": embed in class files</li>
     *     <li>"lazy": load resources using {@link ClassLoader}</li>
     *     <li>"auto": use some heuristic to decide loading strategy</li>
     * </ul>
     * "auto" is the default strategy.
     */
    public static final ConfigDef STRATEGY = new ConfigDef("strategy",
            s -> s.matches("auto|inline|lazy"),
            "Code generation strategy. Valid values: \"auto\"; \"inline\"; \"lazy\".");

    static Set<ConfigDef> set(ConfigDef...defs) {
        Set<ConfigDef> set = new LinkedHashSet<>();
        Collections.addAll(set, defs);
        return set;
    }
}
