// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableInputStreams;

import java.lang.annotation.*;

/**
 * <p>
 *      Directive to generate classpath {@link java.io.InputStream} opening methods from files.
 * </p>
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;InputStreams(value = "foo.bin", name = "Data")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     try (java.io.InputStream in = Data.foo()) {
 *         // etc...
 *     } catch (java.io.IOException e) {
 *         // handle exception
 *     }
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableInputStreams.class)
public @interface InputStreams {
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
    Visibility visibility() default Visibility.PACKAGE;

    /**
     * Generated class name.
     * The final segment of the package name is used when not set.
     *
     * @return name
     */
    String name() default "";
}
