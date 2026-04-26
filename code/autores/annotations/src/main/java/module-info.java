// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * <h2>AutoRes.uk</h2>
 * <p>
 *     Convenient alternative to {@link Class#getResourceAsStream(String)}.
 *     Use for compile-time resource verification or streamlining
 *     resource handling via code generation.
 * </p>
 * <p>
 *     See the {@link uk.autores} package for core functionality.
 * </p>
 *
 * <h3>Java Platform Module System</h3>
 * <p>
 *     <a href="https://dev.java/learn/modules/intro/">Modules</a>
 *     that only use the <code>uk.autores</code> package should generally use
 *     the <code>requires static uk.autores;</code> directive.
 *     This package is intended to be compile-time only.
 * </p>
 * <p>
 *     Modules that extend the API via the <code>{@link uk.autores.handling}</code>
 *     or <code>{@link uk.autores.naming}</code> packages
 *     should generally use the <code>requires uk.autores;</code> directive.
 *  </p>
 *
 *  <h3>Processing Annotations</h3>
 *  <p>
 *      A separate module, <code>uk.autores.processing</code>,
 *      performs the work of processing annotations.
 *      This library must be present on the annotation processor classpath at compile time.
 *      See <a target="_blank" href="https://github.com/autores-uk/autores">source project</a>
 *      for more information.
 *  </p>
 */
module uk.autores {
    // provides annotation processing library
    requires transitive java.compiler;
    // public packages
    exports uk.autores;
    exports uk.autores.handling;
    exports uk.autores.naming;
    exports uk.autores.repeat;
}
