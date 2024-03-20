// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * <h2>AutoRes.uk</h2>
 *
 * <p>
 *     An annotation driven <a href="https://dev.java/learn/modules/intro/">module</a>
 *     for working with classpath resource files.
 * </p>
 * <p>
 *     Modules that only use the <code>uk.autores</code> package should generally use
 *     the <code>requires static {@link uk.autores};</code> directive.
 *     This package is intended to be compile-time only.
 * </p>
 * <p>
 *     Modules that extend the API via the <code>{@link uk.autores.handling}</code> package
 *     should generally use the <code>requires {@link uk.autores};</code> directive.
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
    // annotation processor
    provides javax.annotation.processing.Processor with uk.autores.processors.ResourceFilesProcessor;
    // testable packages
    opens uk.autores to uk.autores.test;
    opens uk.autores.handling to uk.autores.test;
    opens uk.autores.naming to uk.autores.test;
    opens uk.autores.processors to uk.autores.test;
    exports uk.autores.processors to uk.autores.test;
}
