package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Handler;
import uk.autores.processing.Namer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
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
 *     Absolute paths within the bundle may be referenced by setting {@link #relative()} to false and
 *     using an absolute path like <code>"/META-INF/resources/X.txt</code>.
 * </p>
 * <p>
 *     As a minimum this annotation can be used to verify a resource exists without the need for unit tests.
 *     Set {@link #handler()} for other behaviour.
 * </p>
 * <p>
 *     Custom generation or validation can be provided by implementing a {@link Handler}.
 *     Alternative naming strategies can be provided by providing a {@link Namer}.
 *     The compiler must be able to load and instantiate any value set in {@link #handler()} or {@link #namer()}.
 *     This typically means compiling the code in a separate project to the one where they are used.
 * </p>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ResourceFilesRepeater.class)
public @interface ResourceFiles {

    /**
     * This value is passed as location (1st arg) to {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     * The default is {@link StandardLocation#CLASS_PATH}.
     *
     * @return where to search for resources
     */
    StandardLocation location() default StandardLocation.CLASS_PATH;

    /**
     * Defines how the pkg (2nd arg) is to be generated for {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     * If <code>true</code> the package of the annotated type is used.
     * If <code>false</code> the package "" is used.
     *
     * @return true by default
     */
    boolean relative() default true;

    /**
     * Defines the resource files to be processed.
     * <em>In an Apache Maven project these will likely be placed in the <code>src/main/resources</code> directory.</em>
     * Values are passed as relativeName (3rd arg) to {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     *
     * @return the resources to handle
     */
    String[] value();

    /**
     * Enables non-default resource handling.
     *
     * @return the Handler for these resources
     * @see Handler for provided implementations
     */
    Class<? extends Handler> handler() default AssertResourceExists.class;

    /**
     * Set this value to provide alternative class/member naming strategy.
     *
     * @return name generator type
     * @see Namer for provided implementations
     */
    Class<? extends Namer> namer() default Namer.class;

    /**
     * Some implementations of {@link Handler} support configuration.
     *
     * @return configured options
     * @see Handler#config()
     * @see ConfigDefs
     */
    Cfg[] config() default {};

    /**
     * Configuration option.
     *
     * @see Handler#config()
     * @see Handler#validateConfig()
     * @see ConfigDef
     * @see ConfigDefs
     */
    @interface Cfg {
        /**
         * @return name of config option
         * @see ConfigDef#name()
         */
        String key();

        /**
         * @return config option value
         * @see ConfigDef#isValid(String)
         */
        String value();
    }
}