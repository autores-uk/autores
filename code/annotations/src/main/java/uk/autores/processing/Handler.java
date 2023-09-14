package uk.autores.processing;

import java.util.Collections;
import java.util.Set;

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
 * @see uk.autores.ClasspathResource#handler()
 */
public interface Handler {

    /**
     * Handles the context resources.
     *
     * @param context processing context
     * @throws Exception any exception
     * @see Context#resources
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
     * If true the annotation processor will validate {@link uk.autores.ClasspathResource#config()} against {@link #config()}.
     *
     * @return true by default
     */
    default boolean validateConfig() {
        return true;
    }
}
