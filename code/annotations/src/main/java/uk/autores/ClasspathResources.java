package uk.autores;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows {@link ClasspathResource} to be used multiple times on a package or class.
 * API consumers do not need to reference this type.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ClasspathResources {
    ClasspathResource[] value();
}
