// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.GenerateInputStreamsFromFiles;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableInputStreamResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateInputStreamsFromFiles}.
 *
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     &#64;InputStreamResources(value = "foo.bin", name = "Data")
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
@Repeatable(RepeatableInputStreamResources.class)
public @interface InputStreamResources {
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
     * Generated class name.
     * @return class name
     */
    String name() default "";
}
