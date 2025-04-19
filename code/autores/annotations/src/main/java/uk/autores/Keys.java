// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableKeys;

import java.lang.annotation.*;

/**
 * <p>
 *     Directive to generate key constants from {@link java.util.Properties} files.
 * </p>
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     // she-wolf=Cinco lobitos tiene la loba
 *     &#64;Keys("CincoLobitos.properties")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     // "she-wolf"
 *     String key = CincoLobitos.SHE_WOLF;
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableKeys.class)
public @interface Keys {
    /**
     * Resource files.
     * @return resources
     */
    String[] value() default {};

    /**
     * Common processing instructions.
     * @return instruction annotation
     */
    Processing processing() default @Processing(namer = IdiomaticNamer.class);

    /**
     * Generated code visibility.
     * @return visibility
     */
    boolean isPublic() default false;
}
