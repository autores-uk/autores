// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.GenerateByteArraysFromFiles;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableByteArrayResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateByteArraysFromFiles}.
 *
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;ByteArrays("foo.bin")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     byte[] data = Foo.bytes();
 * </code></pre>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableByteArrayResources.class)
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
     * Generated code visibility.
     * @return visibility
     */
    boolean isPublic() default false;

    /**
     * Code generation strategy.
     * @return strategy
     */
    Strategy strategy() default Strategy.AUTO;
}
