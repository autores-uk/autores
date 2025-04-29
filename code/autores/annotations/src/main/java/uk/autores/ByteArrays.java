// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableByteArrays;

import java.lang.annotation.*;

/**
 * <p>
 *     Directive to generate byte arrays from files.
 * </p>
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;ByteArrays(value = "foo.bin", name = "FooData")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     byte[] data = FooData.foo();
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableByteArrays.class)
public @interface ByteArrays {
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
     * Generated class name.
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
}
