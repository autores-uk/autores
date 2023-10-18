package uk.autores.processors;

import uk.autores.processing.Context;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

final class Compiler {

    private static final AtomicBoolean WARN = new AtomicBoolean(true);

    private Compiler() {}

    static void detect(Context ctxt, Supplier<Object> provider) {
        if (provider.get() == null && WARN.getAndSet(false)) {
            ctxt.printWarning("Java compiler not detected");
        }
    }
}
