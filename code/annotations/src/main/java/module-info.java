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
 * <h3>Modules</h3>
 * <p>
 *     <a href="https://dev.java/learn/modules/intro/">Modules</a>
 *     that only use the <code>uk.autores</code> package should generally use
 *     the <code>requires static {@link uk.autores};</code> directive.
 *     This package is intended to be compile-time only.
 * </p>
 * <p>
 *     Modules that extend the API via the <code>{@link uk.autores.handling}</code> package
 *     should generally use the <code>requires {@link uk.autores};</code> directive.
 *  </p>
 */
module uk.autores {
    // provides format library
    requires uk.autores.format;
    // provides annotation processing library
    requires transitive java.compiler;
    // public packages
    exports uk.autores;
    exports uk.autores.handling;
    exports uk.autores.naming;
    exports uk.autores.repeat;
    // annotation processor
    provides javax.annotation.processing.Processor with uk.autores.processors.ResourceFilesProcessor;
    // testable packages
    exports uk.autores.processors to uk.autores.test;
}
