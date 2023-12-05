// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.ResourceFiles;
import uk.autores.ResourceFilesRepeater;
import uk.autores.handling.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static uk.autores.processors.Compare.nullOrEmpty;
import static uk.autores.processors.Compare.sameSeq;

/**
 * Processes classpath resource files and passes them to {@link Handler#handle(Context)}.
 * This type is not intended to be public API but must be visible for annotation processing.
 */
public final class ResourceFilesProcessor extends AbstractProcessor {

  /**
   * Returns {@link ProcessingEnvironment#getSourceVersion()} or
   * the minimum {@link SourceVersion#RELEASE_11}.
   *
   * @return current source version
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    SourceVersion sv = processingEnv.getSourceVersion();
    return Compare.max(SourceVersion.RELEASE_11, sv);
  }

  /**
   * @return ResourceFiles and ResourceFilesRepeater
   * @see ResourceFiles
   * @see ResourceFilesRepeater
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(ResourceFiles.class, ResourceFilesRepeater.class)
            .map(Class::getName)
            .collect(toSet());
  }

  /**
   * Consumes {@link ResourceFiles} and {@link ResourceFilesRepeater} and passes derived information to the
   * specified {@link Handler}.
   *
   * @param annotations the annotation types requested to be processed
   * @param roundEnv  environment for information about the current and prior round
   * @return true if annotation consumed
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    return proc(annotations, roundEnv);
  }

  private boolean proc(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean consumed = false;
    for (TypeElement annotation : annotations) {
      for (Element annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
        consumed = true;

        Name name = annotation.getQualifiedName();
        if (sameSeq(ResourceFiles.class.getName(), name)) {
          ResourceFiles cpr = annotated.getAnnotation(ResourceFiles.class);
          process(cpr, annotated);
        } else if (sameSeq(ResourceFilesRepeater.class.getName(), name)) {
          ResourceFilesRepeater cprs = annotated.getAnnotation(ResourceFilesRepeater.class);
          for (ResourceFiles cpr : cprs.value()) {
            process(cpr, annotated);
          }
        }
      }
    }

    return consumed;
  }

  private void process(ResourceFiles cpr, Element annotated) {
    Handler handler;
    Context context;
    try {
      handler = instance(cpr::handler);
      context = ctxt(cpr, annotated);
    } catch (Exception e) {
      processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR, e.toString(), annotated);
      return;
    }

    if (!handler.validConfig(context.config(), context::printError)) {
      return;
    }

    try {
      handler.handle(context);
    } catch (Exception e) {
      context.printError("PROCESSING EXCEPTION:" + e);
    }
  }

  private List<Resource> resources(ResourceFiles cpr,
                                        Pkg annotationPackage,
                                        Element annotated) {
    List<Resource> resources = new ArrayList<>(cpr.value().length);
    CharSequence pkg = "";
    CharSequence value = "";
    try {
      Filer filer = processingEnv.getFiler();

      for (String resource : cpr.value()) {
        if (nullOrEmpty(resource)) {
          String msg = "Resource paths cannot be null or empty";
          processingEnv.getMessager()
                  .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
          return emptyList();
        }

        pkg = ResourceFiling.pkg(annotationPackage, resource);
        value = ResourceFiling.relativeName(resource);
        FileObject fo = getResource(filer, cpr.locations(), pkg, value);
        try (InputStream is = fo.openInputStream()) {
          // NOOP; if file can be opened it exists
          assert is != null;
        }
        resources.add(new Resource(fo::openInputStream, resource));
      }

    } catch (Exception e) {
      String msg = Errors.resourceErrorMessage(e, value, pkg);
      processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
      return emptyList();
    }

    return resources;
  }

  private FileObject getResource(Filer filer, String[] locations, CharSequence pkg, CharSequence value) throws IOException {
    Set<String> errors = new LinkedHashSet<>();
    for (String location : locations) {
      JavaFileManager.Location jfml = StandardLocation.locationFor(location);
      try {
        FileObject fo = filer.getResource(jfml, pkg, value);
        try (InputStream is = fo.openInputStream()) {
          // NOOP; if file can be opened it exists
          assert is != null;
        }
        return fo;
      } catch (Exception e) {
        errors.add(e.toString());
      }
    }
    String reason = String.join("; ", errors);
    throw new IOException(reason);
  }

  private Context ctxt(ResourceFiles cpr, Element annotated) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Namer namer = instance(cpr::namer);

    List<Config> options = new ArrayList<>();
    for (ResourceFiles.Cfg option : cpr.config()) {
      options.add(new Config(option.key(), option.value()));
    }

    Pkg pkg = pkg(annotated);
    List<Resource> resources = resources(cpr, pkg, annotated);

    return Context.builder()
            .setEnv(processingEnv)
            .setResources(resources)
            .setAnnotated(annotated)
            .setConfig(options)
            .setPkg(pkg(annotated))
            .setNamer(namer)
            .setLocation(locationList(cpr.locations()))
            .build();
  }

  private List<JavaFileManager.Location> locationList(String[] locations) {
    return Stream.of(locations)
            .map(StandardLocation::locationFor)
            .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private <T> T instance(Supplier<Class<T>> s) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    TypeMirror mirror = mirror(s);
    String className = mirror.toString();

    Class<?> c = getClass()
            .getClassLoader()
            .loadClass(className);
    return (T) c.getConstructor()
            .newInstance();
  }

  private TypeMirror mirror(Supplier<?> s) {
    // https://hauchee.blogspot.com/2015/12/compile-time-annotation-processing-getting-class-value.html
    try {
      s.get();
    } catch (MirroredTypeException e) {
      return e.getTypeMirror();
    }
    throw new AssertionError();
  }

  private Pkg pkg(Element annotated) {
    Name qualified = processingEnv.getElementUtils()
            .getPackageOf(annotated)
            .getQualifiedName();
    return Pkg.named(qualified);
  }
}
