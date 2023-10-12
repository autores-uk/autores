package uk.autores;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows {@link ResourceFiles} to be used multiple times on a package or class.
 * API consumers do not need to reference this type - it is only used by the compiler and annotation processor.
 *
 * @see java.lang.annotation.Repeatable
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ResourceFilesRepeater {

    /**
     * @return the individual resource annotations
     */
    ResourceFiles[] value();
}