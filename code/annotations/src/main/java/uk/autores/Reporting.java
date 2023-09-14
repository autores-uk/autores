package uk.autores;

import uk.autores.processing.ConfigDef;
import uk.autores.processing.Context;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.function.Consumer;

final class Reporting {

    private Reporting() {}

    static Consumer<String> reporter(Context context, ConfigDef def) {
        String action = context.option(def.name()).orElse("error");
        if ("ignore".equals(action)) {
            return (m) -> {};
        }
        Messager messager = context.env.getMessager();
        if ("warn".equals(action)) {
            return m -> messager.printMessage(Diagnostic.Kind.WARNING, m, context.annotated);
        }
        return m -> messager.printMessage(Diagnostic.Kind.ERROR, m, context.annotated);
    }
}
