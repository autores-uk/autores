// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.GenerateConstantsFromProperties;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableKeyedResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateConstantsFromProperties}.
 *
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     // she-wolf=Cinco lobitos tiene la loba
 *     &#64;KeyedResources("CincoLobitos.properties")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     // "she-wolf"
 *     String key = CincoLobitos.SHE_WOLF;
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableKeyedResources.class)
public @interface KeyedResources {
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
