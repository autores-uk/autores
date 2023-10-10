package uk.autores.env;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMessager implements Messager {

    public final Map<Diagnostic.Kind, List<CharSequence>> messages = new HashMap<>();

    public TestMessager() {
        for (Diagnostic.Kind kind : Diagnostic.Kind.values()) {
            messages.put(kind, new ArrayList<>());
        }
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
        messages.get(kind).add(msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {
        printMessage(kind, msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        printMessage(kind, msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        printMessage(kind, msg);
    }
}
