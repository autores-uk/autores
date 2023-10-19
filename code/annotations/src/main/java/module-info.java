/**
 * <h1>AutoRes.uk</h1>
 *
 * <p>
 *     An annotation driven <a href="https://dev.java/learn/modules/intro/">module</a>
 *     for working with classpath resource files.
 * </p>
 * <p>
 *     Modules that only use the <code>uk.autores</code> package should generally use
 *     the <code>requires static java.compiler;</code> directive.
 *     This package is intended to be compile-time only.
 * </p>
 * <p>
 *     Modules that extend the API via the <code>uk.autores.processing</code> package
 *     should generally use the <code>requires transitive java.compiler;</code> directive.
 *  </p>
 */
module uk.autores {
    // provides annotation processing library
    requires transitive java.compiler;
    // public packages
    exports uk.autores;
    exports uk.autores.cfg;
    exports uk.autores.processing;
    // annotation processor
    provides javax.annotation.processing.Processor with uk.autores.processors.ResourceFilesProcessor;
    // testable packages
    exports uk.autores.processors to uk.autores.test;
    exports uk.autores.internal to uk.autores.test;
    exports uk.autores.processors.internal to uk.autores.test;
}
