// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

module uk.autores.rhymes.test {
    requires uk.autores.rhymes;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens uk.autores.rhymes.test to org.junit.platform.commons;
}