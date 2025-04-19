// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

module uk.autores.custom.handler {
    requires java.compiler;
    requires transitive uk.autores;
    requires transitive uk.autores.processing;
    requires com.samskivert.jmustache;
    exports uk.autores.custom.handler;
    uses javax.annotation.processing.Processor;
}
