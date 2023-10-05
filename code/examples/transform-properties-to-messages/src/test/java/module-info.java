module uk.autores.messages.test {
    requires uk.autores.messages;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.messages.test to org.junit.platform.commons;
}