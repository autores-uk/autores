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

    public final ProcessingEnvironment env;
    public final JavaFileManager.Location location;
    public final Pkg pkg;
    public final Element annotated;
    public final SortedMap<String, FileObject> resources;
    public final List<Config> config;
    public final Namer namer;

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

    public Optional<String> option(String key) {
        return config.stream()
                .filter(o -> key.equals(o.key()))
                .map(Config::value)
                .findFirst();
    }

    public void printError(String msg) {
        env.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, msg, annotated);
    }
}
