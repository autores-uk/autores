module uk.autores.checked.test {
    requires uk.autores.checked;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.strong.test to org.junit.platform.commons;
}
