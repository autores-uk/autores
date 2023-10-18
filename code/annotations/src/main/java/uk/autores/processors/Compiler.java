package uk.autores.processors;

import uk.autores.processing.Context;

import java.util.function.Supplier;

final class Compiler {

    private Compiler() {}

    static void detect(Context c, Supplier<Object> provider) {
        if (provider.get() == null) {
            c.printWarning("Java compiler not detected");
        }
    }
}
