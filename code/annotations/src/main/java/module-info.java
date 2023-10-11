/** An annotation driven module for working with classpath resource files. */
module uk.autores {
    // provides annotation processing library
    requires transitive java.compiler;
    // public packages
    exports uk.autores;
    exports uk.autores.processing;
    // annotation processor
    provides javax.annotation.processing.Processor with uk.autores.processors.ClasspathResourceProcessor;
    // testable packages
    exports uk.autores.processors to uk.autores.test;
    exports uk.autores.internal to uk.autores.test;
}
