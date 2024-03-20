// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.repeat;

import uk.autores.InputStreamResources;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows {@link uk.autores.InputStreamResources} to be used multiple times on a package or class.
 * API consumers do not need to reference this type - it is only used by the compiler and annotation processor.
 *
 * @see java.lang.annotation.Repeatable
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RepeatableInputStreamResources {

    /**
     * @return the individual resource annotations
     */
    InputStreamResources[] value();
}
