// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableMessages;

import java.lang.annotation.*;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * <p>
 *     Directive to generate localized format methods from {@link java.util.Properties} files.
 * </p>
 * <pre><code>
 *     // EXAMPLE ANNOTATION
 *     // planets.properties
 *     //   planet-event=At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.
 *     // planets_de.properties
 *     //   planet-event=Am {1,time} um {1,date} Uhr gab es {2} auf Planet {0,number,integer}.
 *     &#64;Messages("planets.properties")
 * </code></pre>
 * <pre><code>
 *     // EXAMPLE CODE
 *     var time = ZonedDateTime.now();
 *     String event = Planets.planetEvent(locale, 4, time, "an attack");
 * </code></pre>
 *
 * <p>
 *     See <a href="https://docs.oracle.com/javase/tutorial/i18n/resbundle/propfile.html" target="_blank"
 *     >Backing a {@link java.util.ResourceBundle} with Properties Files</a> for more information on localization.
 * </p>
 * <p>
 *     This annotation is strict by default.
 *     This helps build systems detect missing translations and bugs in translated format strings.
 *     However, developers are likely add or modify strings during development before translations are available.
 *     The {@link #missingKey()} and {@link #incompatibleFormat()} properties can be set to {@link Severity#WARN}
 *     or {@link Severity#IGNORE} during development to avoid breaking builds.
 *     Create a single {@code static final Severity} variable and reference it in annotations to control this globally.
 *     Set these to {@link Severity#ERROR} prior to release and/or in a "smoke test" branch.
 * </p>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableMessages.class)
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
     * @return visibility
     */
    Visibility visibility() default Visibility.PACKAGE;

    /**
     * Search for localized properties.
     *
     * @return whether to localize
     */
    boolean localize() default true;

    /**
     * Treat values as {@link java.text.MessageFormat} expressions and generate typed method format methods.
     *
     * @return whether to format
     */
    boolean format() default true;

    /**
     * How to handle missing keys in localized files.
     * Only applies when {@link #localize()} is true.
     *
     * @return error severity
     */
    Severity missingKey() default Severity.ERROR;

    /**
     * How to handle incompatible format string in localized files.
     * Only applies when {@link #format()} is true.
     *
     * @return error severity
     */
    Severity incompatibleFormat() default Severity.ERROR;

    /**
     * Signature type where format expression has no type.
     * Set this to broaden to {@link Object#toString()}
     * or narrow to {@link String}.
     *
     * @return type used in generated methods
     */
    Class<?> noneType() default CharSequence.class;

    /**
     * Signature type where format expression is "number" or "choice" type.
     * Set this to use stricter {@link Number} types.
     *
     * @return type used in generated methods
     */
    Class<? extends Number> numberType() default Number.class;

    /**
     * Signature type where format expression is date/time type.
     * Set this to relax rules on input.
     * Using inappropriate {@link TemporalAccessor} types with some expressions
     * can result in {@link java.time.DateTimeException}.
     *
     * @return type used in generated methods
     */
    Class<? extends TemporalAccessor> dateTimeType() default ZonedDateTime.class;
}
