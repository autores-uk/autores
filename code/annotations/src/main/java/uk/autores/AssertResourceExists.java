package uk.autores;

import uk.autores.processors.ClasspathResourceProcessor;

/**
 * A resource handler that does nothing.
 * This is useful to assert a resource exists.
 * {@link ClasspathResourceProcessor} errors when a resource is not found.
 */
public final class AssertResourceExists implements Handler {

    @Override
    public void handle(Context context) {
        // NOOP
    }
}
