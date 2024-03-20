package uk.autores;

import uk.autores.repeat.RepeatableStringResources;

import java.lang.annotation.*;

/**
 * Annotation for {@link GenerateStringsFromText}.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RepeatableStringResources.class)
public @interface StringResources {
    /**
     * Resource files.
     * @return text resources
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

    /**
     * Resource text encoding.
     * @return canonical encoding name
     */
    String encoding() default "UTF-8";
}
