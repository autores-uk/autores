// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableMessageResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateMessagesFromProperties}.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableMessageResources.class)
public @interface MessageResources {
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
     * Whether generated code is public.
     * @return visibility
     */
    boolean isPublic() default false;

    /**
     * When true, searches for localized properties.
     *
     * @return whether to localize
     */
    boolean localize() default true;

    /**
     * When true, generates format signatures.
     *
     * @return whether to format
     */
    boolean format() default true;

    /**
     * How to handle missing keys in localized files.
     *
     * @return error severity
     */
    Severity missingKey() default Severity.ERROR;
}
