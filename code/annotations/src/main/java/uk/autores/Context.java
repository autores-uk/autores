package uk.autores;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

import static java.util.Objects.requireNonNull;

/**
 * Context information for processing resource files.
 */
public final class Context {

    /**
     * Annotation processing environment.
     */
    public final ProcessingEnvironment env;
    /**
     * Where resources are to be loaded from.
     * @see ClasspathResource#location()
     * @see ProcessingEnvironment#getFiler()
     * @see javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)
     */
    public final JavaFileManager.Location location;
    /**
     * Package of the annotated type.
     */
    public final Pkg pkg;
    /**
     * The annotated element - class or package.
     *
     * @see javax.lang.model.element.TypeElement
     * @see javax.lang.model.element.PackageElement
     */
    public final Element annotated;
    /**
     * The resources.
     * @see ClasspathResource#value()
     */
    public final SortedMap<String, FileObject> resources;
    /**
     * The configuration.
     * @see ClasspathResource#config()
     */
    public final List<Config> config;
    /**
     * The name resolver.
     * @see ClasspathResource#namer()
     */
    public final Namer namer;

    /**
     * @param env annotation processing environment
     * @param location where to search for resources
     * @param pkg package of the annotated type
     * @param annotated the annotated element
     * @param resources the resources defined in the annotation
     * @param config configuration defined in the annotation
     * @param namer name resolver
     */
    public Context(ProcessingEnvironment env,
                   JavaFileManager.Location location,
                   Pkg pkg,
                   Element annotated,
                   SortedMap<String, FileObject> resources,
                   List<Config> config,
                   Namer namer) {
        this.env = env;
        this.location = requireNonNull(location, "location");
        this.pkg = pkg;
        this.annotated = requireNonNull(annotated, "annotatedElement");
        this.resources = requireNonNull(resources, "resources");
        this.config = requireNonNull(config, "config");
        this.namer = requireNonNull(namer, "namer");
    }

    /**
     * @param key key name
     * @return the value if present
     * @see ClasspathResource.Cfg
     * @see ConfigDefs
     */
    public Optional<String> option(String key) {
        return config.stream()
                .filter(o -> key.equals(o.key()))
                .map(Config::value)
                .findFirst();
    }

    /**
     * Convenience method for sending an error to the {@link javax.annotation.processing.Messager}.
     *
     * @param msg the error message to be emitted
     * @see ProcessingEnvironment#getMessager()
     */
    public void printError(String msg) {
        env.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
    }
}
