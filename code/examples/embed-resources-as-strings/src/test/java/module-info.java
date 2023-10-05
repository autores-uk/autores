module uk.autores.strings.test {
    requires uk.autores.strings;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.strings.test to org.junit.platform.commons;
}