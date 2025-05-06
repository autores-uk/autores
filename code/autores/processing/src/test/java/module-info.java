// Copyright 2025 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

module uk.autores.processing.test {
    requires uk.autores;
    requires uk.autores.processing;
    requires uk.autores.format;
    requires java.compiler;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;
    requires org.jooq.joor;

    opens uk.autores.processing.test to org.junit.platform.commons;
    opens uk.autores.processing.test.handlers to org.junit.platform.commons;
    opens uk.autores.processing.test.handlers.debug to org.junit.platform.commons;

    exports uk.autores.processing.test;
}
