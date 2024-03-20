package uk.autores;

import uk.autores.naming.IdiomaticNamer;
import uk.autores.repeat.RepeatableKeyedResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateConstantsFromProperties}.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableKeyedResources.class)
public @interface KeyedResources {
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
}
