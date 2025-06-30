// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableTexts;

import java.lang.annotation.*;

/**
 * <p>
 *     Directive to generate strings from text files.
 * </p>
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;Texts(value = "Roses.txt", name = "Flowers")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     String roses = Flowers.roses();
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableTexts.class)
public @interface Texts {
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
     * Generated class name.
     * The final segment of the package name is used when not set.
     *
     * @return name
     */
    String name() default "";

    /**
     * Generated code visibility.
     * @return visibility
     */
    Visibility visibility() default Visibility.PACKAGE;

    /**
     * Code generation strategy.
     * @return strategy
     */
    Strategy strategy() default Strategy.AUTO;

    /**
     * Resource <a href="https://en.wikipedia.org/wiki/Character_encoding" target="_blank">character encoding</a>.
     * Character decoding is strict.
     * Malformed characters result in errors.
     *
     * @return canonical encoding name
     */
    String encoding() default "UTF-8";
}
