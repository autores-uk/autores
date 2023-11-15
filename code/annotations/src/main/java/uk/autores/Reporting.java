package uk.autores;

import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.function.Consumer;

/** Utility type to aid in optional error reporting. */
final class Reporting {

    private Reporting() {}

    static Consumer<String> reporter(Context context, ConfigDef def) {
        String action = context.option(def).orElse("error");
        if ("ignore".equals(action)) {
            return (m) -> {};
        }
        Messager messager = context.env().getMessager();
        Element annotated = context.annotated();
        if ("warn".equals(action)) {
            return m -> messager.printMessage(Diagnostic.Kind.WARNING, m, annotated);
        }
        return m -> messager.printMessage(Diagnostic.Kind.ERROR, m, annotated);
    }
}
