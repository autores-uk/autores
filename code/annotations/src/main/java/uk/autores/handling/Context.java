package uk.autores.handling;

import uk.autores.ResourceFiles;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Context information for processing resource files.
 */
public final class Context {

    private final ProcessingEnvironment env;
    private final JavaFileManager.Location location;
    private final Pkg pkg;
    private final Element annotated;
    private final List<Resource> resources;
    private final List<Config> config;
    private final Namer namer;

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
                   List<Resource> resources,
                   List<Config> config,
                   Namer namer) {
        this.env = env;
        this.location = requireNonNull(location, "location");
        this.pkg = pkg;
        this.annotated = requireNonNull(annotated, "annotatedElement");
        this.resources = unmodifiableList(requireNonNull(resources, "resources"));
        this.config = unmodifiableList(requireNonNull(config, "config"));
        this.namer = requireNonNull(namer, "namer");
    }

    /**
     * @param key key name
     * @return the value if present
     * @see ResourceFiles.Cfg
     */
    public Optional<String> option(ConfigDef key) {
        return config.stream()
                .filter(o -> key.key().equals(o.key()))
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

    /**
     * @return annotation processing environment
     */
    public ProcessingEnvironment env() {
        return env;
    }

    /**
     * @return where resources are to be loaded from
     * @see ResourceFiles#location()
     * @see ProcessingEnvironment#getFiler()
     * @see javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)
     */
    public JavaFileManager.Location location() {
        return location;
    }

    /**
     * The package of the annotated type.
     *
     * @return package information
     */
    public Pkg pkg() {
        return pkg;
    }

    /**
     * @return the annotated element - class or package
     * @see javax.lang.model.element.TypeElement
     * @see javax.lang.model.element.PackageElement
     */
    public Element annotated() {
        return annotated;
    }

    /**
     * @return unmodifiable resources set
     * @see ResourceFiles#value()
     */
    public List<Resource> resources() {
        return resources;
    }

    /**
     * @return unmodifiable configuration
     * @see ResourceFiles#config()
     */
    public List<Config> config() {
        return config;
    }

    /**
     * @return  name resolver
     * @see ResourceFiles#namer()
     */
    public Namer namer() {
        return namer;
    }
}