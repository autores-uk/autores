package uk.autores.processors;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;

final class AnnotationDef<S extends Annotation, R extends Annotation> {
    final Class<S> single;
    final Class<R> repeating;
    final F<S, R> factory;

    public AnnotationDef(Class<S> single, Class<R> repeating, F<S, R> factory) {
        this.single = single;
        this.repeating = repeating;
        this.factory = factory;
    }

    Stream<Class<?>> annotations() {
        return Stream.of(single, repeating);
    }

    @FunctionalInterface
    interface F<S extends Annotation, R extends Annotation> {
        ContextFactory<S, R> create(ProcessingEnvironment env);
    }
}
