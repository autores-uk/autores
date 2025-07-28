// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.naming.Namer;
import uk.autores.processing.handlers.ResourceFiling;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.autores.processing.Compare.nullOrEmpty;

abstract class ContextFactory<S extends Annotation, R extends Annotation> {

    private final ProcessingEnvironment env;
    private final Class<S> single;
    private final Class<R> repeating;

    ContextFactory(ProcessingEnvironment env, Class<S> single, Class<R> repeating) {
        this.env = env;
        this.single = single;
        this.repeating = repeating;
    }

    boolean supported(Name annotationName) {
        return Compare.sameSeq(annotationName, single.getName())
                || Compare.sameSeq(annotationName, repeating.getName());
    }

    List<Pair> contexts(Name name, Element annotated) {
        List<Pair> contexts = new ArrayList<>();
        List<S> annotations;
        if (Compare.sameSeq(name, single.getName())) {
            annotations = Collections.singletonList(annotated.getAnnotation(single));
        } else {
            R r = annotated.getAnnotation(repeating);
            annotations = Arrays.asList(expand(r));
        }
        for (S s : annotations) {
            Processing p = processing(s);
            Context builder = init(s, annotated, p);
            Handler handler = handler(s);
            contexts.add(new Pair(builder, handler));
        }
        return contexts;
    }

    abstract Handler handler(S single);

    abstract List<Config> config(S s);

    abstract S[] expand(R repeating);

    abstract Processing processing(S single);

    abstract String[] resources(S single);

    private Context init(S s, Element annotated, Processing p) {
        Pkg pkg = pkg(annotated);
        List<Resource> r = resources(resources(s), p.locations(), pkg, annotated);
        Namer namer = instance(p::namer);
        List<Config> configs = config(s);
        List<JavaFileManager.Location> locations = locationList(p.locations());
        return Context.builder()
                .setAnnotated(annotated)
                .setConfig(configs)
                .setEnv(env)
                .setLocation(locations)
                .setNamer(namer)
                .setPkg(pkg)
                .setResources(r)
                .build();
    }

    private Pkg pkg(Element annotated) {
        Name qualified = env.getElementUtils()
                .getPackageOf(annotated)
                .getQualifiedName();
        return Pkg.named(qualified);
    }


    @SuppressWarnings("unchecked")
    protected <T> T instance(Supplier<Class<T>> s) {
        TypeMirror mirror = mirror(s);
        String className = mirror.toString();

        try {
            Class<?> c = getClass()
                    .getClassLoader()
                    .loadClass(className);
            return (T) c.getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String name(Supplier<Class<?>> s) {
        TypeMirror tm = mirror(s);
        Types types = env.getTypeUtils();
        TypeElement element = (TypeElement) types.asElement(tm);
        return element.getQualifiedName().toString();
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

    private List<Resource> resources(String[] res,
                             String[] locations,
                             Pkg annotationPackage,
                             Element annotated) {
        var resources = new ArrayList<Resource>(res.length);
        CharSequence pkg = "";
        CharSequence value = "";
        try {
            var filer = env.getFiler();

            for (String resource : res) {
                if (nullOrEmpty(resource)) {
                    String msg = "Resource paths cannot be null or empty";
                    env.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
                    return emptyList();
                }

                pkg = ResourceFiling.pkg(annotationPackage, resource);
                value = ResourceFiling.relativeName(resource);
                FileObject fo = getResource(filer, locations, pkg, value);
                try (InputStream is = fo.openInputStream()) {
                    // NOOP; if file can be opened it exists
                    assert is != null;
                }
                resources.add(new Resource(fo::openInputStream, resource));
            }

        } catch (Exception e) {
            String msg = Errors.resourceErrorMessage(e, value, pkg);
            env.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
            return emptyList();
        }

        return resources;
    }

    private FileObject getResource(Filer filer, String[] locations, CharSequence pkg, CharSequence value) throws IOException {
        var errors = new LinkedHashSet<String>();
        for (String location : locations) {
            var jfml = StandardLocation.locationFor(location);
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

    private List<JavaFileManager.Location> locationList(String[] locations) {
        return Stream.of(locations)
                .map(StandardLocation::locationFor)
                .toList();
    }

    record Pair(Context context, Handler handler) {
    }
}
