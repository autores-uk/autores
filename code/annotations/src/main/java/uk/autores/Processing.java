package uk.autores;

import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

/**
 * Common processing instructions.
 * The compiler must be able to load and instantiate any value set in {@link #namer()}.
 */
public @interface Processing {
    /**
     * Set this value to provide alternative class/member naming strategy.
     *
     * @return name generator type
     * @see Namer Namer for provided implementations
     */
    Class<? extends Namer> namer() default Namer.class;

    /**
     * <p>
     *     These values are passed as location (1st arg) to
     *     {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}
     *     in order.
     * </p>
     * <p>
     *     The defaults are {@link StandardLocation#CLASS_PATH} and {@link StandardLocation#CLASS_OUTPUT} names.
     * </p>
     * <p>
     *     Tools are inconsistent in how they locate resources.
     *     The documentation for {@link Filer} states
     *     <em>"The locations CLASS_OUTPUT and SOURCE_OUTPUT must be supported."</em>
     *     If the filer throws an {@link IllegalArgumentException} the processor will
     *     proceed to the next location.
     * </p>
     *
     * @return where to search for resources
     * @see StandardLocation#locationFor(String)
     * @see StandardLocation#getName()
     */
    String[] locations() default {
            "CLASS_OUTPUT", // IntelliJ likes this
            "CLASS_PATH", // Maven likes this, Eclipse compiler throws IllegalArgumentException
    };
}
