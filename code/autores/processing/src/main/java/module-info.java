// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

import javax.annotation.processing.Processor;

/**
 * <p>
 *     Annotation processor for the {@link uk.autores} API.
 *  </p>
 */
module uk.autores.processing {
    // provides format library
    requires uk.autores;
    requires uk.autores.format;
    // provides annotation processing library
    requires transitive java.compiler;
    // public packages
    exports uk.autores.processing;
    exports uk.autores.processing.handlers;
    // annotation processor
    provides Processor with uk.autores.processing.ResourceFilesProcessor;
}
