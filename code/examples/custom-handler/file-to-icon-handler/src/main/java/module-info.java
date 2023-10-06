module uk.autores.custom.handler {
    requires java.compiler;
    requires transitive uk.autores;
    requires com.samskivert.jmustache;
    exports uk.autores.custom.handler;
    uses javax.annotation.processing.Processor;
}
