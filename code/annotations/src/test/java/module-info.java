module uk.autores.test {
    requires uk.autores;
    requires java.compiler;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.jooq.joor;

    opens uk.autores.test to org.junit.platform.commons;
    opens uk.autores.test.internal to org.junit.platform.commons;
    opens uk.autores.test.processing to org.junit.platform.commons;
    opens uk.autores.test.processors to org.junit.platform.commons;

    exports uk.autores.test.processors;
}
