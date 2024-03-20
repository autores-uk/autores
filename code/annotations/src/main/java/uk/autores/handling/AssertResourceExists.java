// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.processors.ResourceFilesProcessor;

/**
 * A resource handler that does nothing but can be used to ensure a resource exists at compile time.
 * {@link ResourceFilesProcessor} errors when a resource is not found.
 */
public final class AssertResourceExists implements Handler {

    /**
     * Does nothing.
     * @param context processing context
     */
    @Override
    public void handle(Context context) {
        // NOOP
    }
}
