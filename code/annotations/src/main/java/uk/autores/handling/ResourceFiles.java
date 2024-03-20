// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.Processing;
import uk.autores.naming.Namer;
import uk.autores.repeat.RepeatableResourceFiles;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileManager;
import java.lang.annotation.*;

/**
 * <p>
 *     Indicates resources that are to be processed at compile time.
 * </p>
 * {@code @ResourceFiles("some-resource.txt")}
 * <p>
 *     The compiler must be able to load referenced resources using
 *     {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
 * </p>
 * <p>
 *     It is recommended that resources be placed in a bundled directory path equivalent to the type or package.
 *     For example, annotated class <code>foo.bar.Baz</code> can reference the relative resource
 *     <code>foo/bar/X.txt</code> with the string <code>"X.txt"</code>.
 * </p>
 * <p>
 *     Absolute paths like <code>"/META-INF/resources/X.txt</code> must be start with a forward slash.
 * </p>
 * <p>
 *     As a minimum this annotation can be used to verify a resource exists without the need for unit tests.
 *     Set {@link #handler()} for other behaviour.
 * </p>
 * <p>
 *     Custom generation or validation can be provided by implementing a {@link Handler}.
 *     Alternative naming strategies can be set by providing a {@link Namer} via {@link #processing()}.
 *     The compiler must be able to load and instantiate any value set in {@link #handler()}.
 *     This typically means compiling the code in a separate project to the one where they are used.
 * </p>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableResourceFiles.class)
public @interface ResourceFiles {

    /**
     * Defines the resource files to be processed.
     * <em>In an Apache Maven project these will likely be placed in the <code>src/main/resources</code> directory.</em>
     *
     * @return the resources to handle
     */
    String[] value();

    /**
     * Enables non-default resource handling.
     *
     * @return the Handler for these resources
     * @see Handler Handler for provided implementations
     */
    Class<? extends Handler> handler() default AssertResourceExists.class;

    /**
     * Common processing instructions
     *
     * @return processing instructions
     */
    Processing processing() default @Processing(namer = Namer.class);

    /**
     * Some implementations of {@link Handler} support configuration.
     *
     * @return configured options
     * @see Handler#config()
     * @see Sets
     */
    Cfg[] config() default {};

    /**
     * Configuration option.
     *
     * @see Handler#config()
     * @see ConfigDef
     */
    @interface Cfg {
        /**
         * @return name of config option
         * @see ConfigDef#key()
         */
        String key();

        /**
         * @return config option value
         * @see ConfigDef#isValid(String)
         */
        String value();
    }
}
