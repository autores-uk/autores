module uk.autores {
    requires transitive java.compiler;

    exports uk.autores;
    exports uk.autores.processing;
    exports uk.autores.processors;

    exports uk.autores.internal to uk.autores.test;

    provides javax.annotation.processing.Processor with uk.autores.processors.ClasspathResourceProcessor;
}
