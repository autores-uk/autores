// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.handling.GenerateMessagesFromProperties;
import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableMessageResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateMessagesFromProperties}.
 *
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     // messages.properties
 *     // planet-event=At {1,time} on {2,date}, there was {3} on planet {0,number,integer}.
 *     // messages_de.properties
 *     // planet-event=Am {1,time} um {2,date} Uhr gab es {3} auf Planet {0,number,integer}.
 *     &#64;Messages("messages.properties")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     ZonedDateTime time = ...
 *     String event = Messages.planetEvent(locale, 4, time, time, "an attack");
 * </code></pre>
 *
 * <p>
 *     See <a href="https://docs.oracle.com/javase/tutorial/i18n/resbundle/propfile.html"
 *     >Backing a {@link java.util.ResourceBundle} with Properties Files</a> for more information on localization.
 * </p>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableMessageResources.class)
public @interface Messages {
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
     *
     * @return visibility
     */
    boolean isPublic() default false;

    /**
     * Search for localized properties.
     *
     * @return whether to localize
     */
    boolean localize() default true;

    /**
     * Generate format signatures.
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
