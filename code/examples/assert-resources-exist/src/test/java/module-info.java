module uk.autores.exist.test {
    requires uk.autores.exist;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.exist.test to org.junit.platform.commons;
}