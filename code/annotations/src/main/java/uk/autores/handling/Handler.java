// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

/**
 * Processes resources from the class path.
 * Implementations MUST:
 * <ul>
 *     <li>Be public</li>
 *     <li>Have a public no-args constructor</li>
 *     <li>Be available as compiled types on the compiler classpath</li>
 * </ul>
 * Implementations inherit the limitations of {@link javax.annotation.processing.Processor}.
 * That is:
 * <ol>
 *     <li>The result of processing a given input is not a function of the presence or absence of other inputs (orthogonality)</li>
 *     <li>Processing the same input produces the same output (consistency)</li>
 *     <li>Processing input A followed by processing input B is equivalent to processing B then A (commutativity)</li>
 *     <li>Processing an input does not rely on the presence of the output of other annotation processors (independence)</li>
 * </ol>
 *
 * @see ResourceFiles#handler()
 */
public interface Handler {

    /**
     * Handles the context resources.
     *
     * @param context processing context
     * @throws Exception any exception
     * @see Context#resources()
     */
    void handle(Context context) throws Exception;

    /**
     * The configuration supported by this handler.
     *
     * @return empty set by default
     */
    default Set<ConfigDef> config() {
        return Collections.emptySet();
    }

    /**
     * <p>
     *     Validates the {@link Context#config()}. Override to change behaviour.
     * </p>
     * <p>
     *     The default implementation returns true if:
     * </p>
     * <ul>
     *     <li>Key names are unique</li>
     *     <li>Key names match {@link #config()} {@link ConfigDef#key()}</li>
     *     <li>Values pass {@link #config()} {@link ConfigDef#isValid(String)}</li>
     * </ul>
     *
     * @param config handler configuration
     * @param errorReporter error message consumer
     * @return true if the config is valid for this handler
     */
    default boolean validConfig(List<Config> config, Consumer<String> errorReporter) {
        Map<String, ConfigDef> defs = config()
                .stream()
                .collect(toMap(ConfigDef::key, cd -> cd));
        Set<String> configured = new HashSet<>();

        for (Config option : config) {
            String key = option.key();
            if (configured.contains(key)) {
                errorReporter.accept("Duplicate config '" + option + "'");
                return false;
            }
            configured.add(key);

            ConfigDef def = defs.get(key);
            if (def == null) {
                errorReporter.accept("Unknown config '" + option + "'");
                return false;
            }
            if (!def.isValid(option.value())) {
                errorReporter.accept("Invalid config '" + option + "'");
                return false;
            }
        }

        return true;
    }

}
