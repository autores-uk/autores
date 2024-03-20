package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableInputStreamResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateInputStreamsFromFiles}.
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
     * Whether generated code is public.
     * @return visibility
     */
    boolean isPublic() default false;

    /**
     * Generated class name.
     * @return class name
     */
    String name() default "";
}
