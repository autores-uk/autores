package uk.autores.processors;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class DecoratedProcessor implements Processor {

    boolean invoked;

    private final Processor processor;

    public DecoratedProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return processor.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return processor.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processor.getSupportedSourceVersion();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        processor.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        invoked = true;
        return processor.process(annotations, roundEnv);
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return processor.getCompletions(element, annotation, member, userText);
    }
}
