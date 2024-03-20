package uk.autores;

import uk.autores.repeat.RepeatableByteArrayResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateByteArraysFromFiles}.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableByteArrayResources.class)
public @interface ByteArrayResources {
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
     * Code generation strategy.
     * @return strategy
     */
    Strat strategy() default Strat.AUTO;
}
