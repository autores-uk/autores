// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.ResourceFiles;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;
import uk.autores.repeat.RepeatableResourceFiles;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Processes classpath resource files and passes them to {@link Handler#handle(Context)}.
 * This type is not intended to be public API but must be visible for annotation processing.
 */
public final class ResourceFilesProcessor extends AbstractProcessor {

  private final AnnotationDef<?, ?>[] defs = {
          ResourceContexts.def(),
          ByteArrayContexts.def(),
          InputStreamContexts.def(),
          KeyedContexts.def(),
          MessageContexts.def(),
          StringContexts.def(),
  };

  /**
   * Returns {@link ProcessingEnvironment#getSourceVersion()} or
   * the minimum {@link SourceVersion#RELEASE_8}.
   *
   * @return current source version
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    SourceVersion sv = processingEnv.getSourceVersion();
    return Compare.max(SourceVersion.RELEASE_8, sv);
  }

  /**
   * Supported annotations.
   *
   * @return {@link ResourceFiles} and {@link RepeatableResourceFiles}
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(defs)
            .flatMap(AnnotationDef::annotations)
            .map(Class::getName)
            .collect(toSet());
  }

  /**
   * Consumes {@link ResourceFiles} and {@link RepeatableResourceFiles} and passes derived information to the
   * specified {@link Handler}.
   *
   * @param annotations the annotation types requested to be processed
   * @param roundEnv  environment for information about the current and prior round
   * @return true if annotation consumed
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<ContextFactory<?, ?>> factories = contextFactories();

    boolean consumed = false;
    for (TypeElement annotation : annotations) {
      for (Element annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
        consumed = true;

        processAnnotation(factories, annotation, annotated);
      }
    }

    return consumed;
  }

  private void processAnnotation(List<ContextFactory<?, ?>> factories, TypeElement annotation, Element annotated) {
    for (ContextFactory<?, ?> factory : factories) {
      Name name = annotation.getQualifiedName();
      if (factory.supported(name)) {
        processResources(name, annotated, factory);
        break;
      }
    }
  }

  private void processResources(Name name, Element annotated, ContextFactory<?, ?> factory) {
    try {
      for (ContextFactory.Pair pair : factory.contexts(name, annotated)) {
        Handler handler = pair.handler;
        Context context = pair.context;

        if (!handler.validConfig(context.config(), context::printError)) {
          return;
        }

        handler.handle(context);
      }
    } catch (Exception e) {
      processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR, e.toString(), annotated);
    }
  }

  private List<ContextFactory<?, ?>> contextFactories() {
    return Stream.of(defs)
            .map(d -> d.factory.create(processingEnv))
            .collect(toList());
  }
}
