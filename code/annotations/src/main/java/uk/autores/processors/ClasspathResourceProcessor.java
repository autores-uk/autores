package uk.autores.processors;

import uk.autores.ClasspathResource;
import uk.autores.ClasspathResources;
import uk.autores.internal.CharSeq;
import uk.autores.internal.OptionValidation;
import uk.autores.internal.ResPath;
import uk.autores.processing.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Processes classpath resource files and passes them to {@link Handler#handle(Context)}.
 * This type is not intended to be public API but must be visible for annotation processing.
 */
public final class ClasspathResourceProcessor extends AbstractProcessor {

  /**
   * {@link SourceVersion#RELEASE_11}.
   *
   * @return RELEASE_11
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_11;
  }

  /**
   * ClasspathResource &amp; ClasspathResources
   *
   * @return ClasspathResource and ClasspathResources
   * @see ClasspathResource
   * @see ClasspathResources
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(ClasspathResource.class, ClasspathResources.class)
            .map(Class::getName)
            .collect(toSet());
  }

  /**
   * Consumes {@link ClasspathResource} and {@link ClasspathResources} and passes derived information to the
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
        if (CharSeq.equivalent(ClasspathResource.class.getName(), name)) {
          ClasspathResource cpr = annotated.getAnnotation(ClasspathResource.class);
          process(cpr, annotated);
        } else if (CharSeq.equivalent(ClasspathResources.class.getName(), name)) {
          ClasspathResources cprs = annotated.getAnnotation(ClasspathResources.class);
          for (ClasspathResource cpr : cprs.value()) {
            process(cpr, annotated);
          }
        }
      }
    }

    return consumed;
  }

  private void process(ClasspathResource cpr, Element annotated) {
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

    if (!OptionValidation.areValid(handler, context)) {
      return;
    }

    try {
      handler.handle(context);
    } catch (Exception e) {
      String msg = "ERROR:";
      msg += " Location: " + context.location();
      msg += " Package:" + context.pkg().name();
      msg += " Exception: " + e;
      processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
    }
  }

  private SortedSet<Resource> resources(ClasspathResource cpr,
                                                  Pkg pkg,
                                                  Element annotated) {
    SortedSet<Resource> map = new TreeSet<>();
    String value = "";
    try {
      Filer filer = processingEnv.getFiler();

      for (String resource : cpr.value()) {
        String filerPath = ResPath.massage(processingEnv, annotated, pkg, resource);
        if (filerPath == null) {
          continue;
        }

        value = filerPath;
        FileObject fo = filer.getResource(cpr.location(), pkg.resourcePackage(), value);
        try (InputStream is = fo.openInputStream()) {
          // NOOP; if file can be opened it exists
          assert is != null;
        }
        map.add(new Resource(fo, resource));
      }

    } catch (Exception e) {
      String msg = "ERROR:";
      msg += " Location: " + cpr.location();
      msg += " Resource: " + value;
      msg += " Exception: " + e;
      processingEnv.getMessager()
              .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
      return Collections.emptySortedSet();
    }

    return map;
  }

  private Context ctxt(ClasspathResource cpr, Element annotated) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Namer namer = instance(cpr::namer);

    List<Config> options = new ArrayList<>();
    for (ClasspathResource.Cfg option : cpr.config()) {
      options.add(new Config(option.key(), option.value()));
    }

    Pkg pkg = pkg(cpr.relative(), annotated);
    SortedSet<Resource> resources = resources(cpr, pkg, annotated);

    return new Context(
            processingEnv,
            cpr.location(),
            pkg(cpr.relative(), annotated),
            annotated,
            resources,
            options,
            namer
    );
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

  private Pkg pkg(boolean relative, Element annotated) {
    String qualified = processingEnv.getElementUtils()
            .getPackageOf(annotated)
            .getQualifiedName()
            .toString();
    return new Pkg(qualified, relative);
  }
}
