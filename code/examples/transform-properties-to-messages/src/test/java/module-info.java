// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

module uk.autores.messages.test {
    requires uk.autores.messages;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.messages.test to org.junit.platform.commons;
}