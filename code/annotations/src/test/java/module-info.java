// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

module uk.autores.test {
    requires uk.autores;
    requires java.compiler;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;
    requires org.jooq.joor;

    opens uk.autores.test to org.junit.platform.commons;
    opens uk.autores.test.cfg to org.junit.platform.commons;
    opens uk.autores.test.handling to org.junit.platform.commons;
    opens uk.autores.test.naming to org.junit.platform.commons;
    opens uk.autores.test.processors to org.junit.platform.commons;

    exports uk.autores.test.processors;
}
