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
 * {@code @ClasspathResource("some-resource.txt")}
 * <p>
 *     As a minimum this annotation can be used to verify a resource exists without the need for unit tests.
 *     Specify a {@link #handler()} for other behaviour.
 * </p>
 * <p>
 *     The compiler must be able to load and instantiate any value set in {@link #handler()} or {@link #namer()}.
 *     This typically means compiling the code in a separate project to the one where they are used.
 * </p>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ClasspathResources.class)
public @interface ClasspathResource {

    /**
     * This value is passed as location (1st arg) to {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
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
