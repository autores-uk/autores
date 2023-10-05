module uk.autores.test {
    requires transitive uk.autores;
    requires transitive java.compiler;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires transitive org.jooq.joor;

    opens uk.autores.test to org.junit.platform.commons;
    opens uk.autores.test.internal to org.junit.platform.commons;
    opens uk.autores.test.processing to org.junit.platform.commons;
    opens uk.autores.test.processors to org.junit.platform.commons;

    exports uk.autores.test.processors;
}
