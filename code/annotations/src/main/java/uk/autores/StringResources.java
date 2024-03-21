// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.GenerateStringsFromText;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableStringResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateStringsFromText}.
 *
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;StringResources("Roses.txt")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     String roses = Roses.text();
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableStringResources.class)
public @interface StringResources {
    /**
     * Resource files.
     * @return text resources
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

    /**
     * Code generation strategy.
     * @return strategy
     */
    Strategy strategy() default Strategy.AUTO;

    /**
     * Resource text encoding.
     * @return canonical encoding name
     */
    String encoding() default "UTF-8";
}
