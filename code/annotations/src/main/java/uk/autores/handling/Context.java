// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Context information for processing resource files.
 * Instances are created by the annotation processor and passed to {@link Handler#handle(Context)}.
 */
public final class Context {

    private final ProcessingEnvironment env;
    private final List<JavaFileManager.Location> locations;
    private final Pkg pkg;
    private final Element annotated;
    private final List<Resource> resources;
    private final List<Config> config;
    private final Namer namer;

    /**
     * @param env annotation processing environment
     * @param locations where to search for resources
     * @param pkg package of the annotated type
     * @param annotated the annotated element
     * @param resources the resources defined in the annotation
     * @param config configuration defined in the annotation
     * @param namer name resolver
     */
    private Context(ProcessingEnvironment env,
                   List<JavaFileManager.Location> locations,
                   Pkg pkg,
                   Element annotated,
                   List<Resource> resources,
                   List<Config> config,
                   Namer namer) {
        this.env = requireNonNull(env, "env");
        this.locations = unmodifiableList(requireNonNull(locations, "locations"));
        this.pkg = requireNonNull(pkg, "pkg");
        this.annotated = requireNonNull(annotated, "annotatedElement");
        this.resources = unmodifiableList(requireNonNull(resources, "resources"));
        this.config = unmodifiableList(requireNonNull(config, "config"));
        this.namer = requireNonNull(namer, "namer");
    }

    /**
     * @return new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} initialized from current state.
     *
     * @return new builder
     */
    public Builder rebuild() {
        return new Builder(this);
    }

    /**
     * First {@link Config#value()} where {@link Config#value()} equals {@link ConfigDef#key()}.
     *
     * @param def name provider
     * @return the value if present
     * @see ResourceFiles.Cfg
     */
    public Optional<String> option(ConfigDef def) {
        return config.stream()
                .filter(o -> def.key().equals(o.key()))
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
     * @see ResourceFiles#locations()
     * @see ProcessingEnvironment#getFiler()
     * @see javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)
     */
    public List<JavaFileManager.Location> locations() {
        return locations;
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
     * @return unmodifiable resources list
     * @see ResourceFiles#value()
     */
    public List<Resource> resources() {
        return resources;
    }

    /**
     * @return unmodifiable configuration list
     * @see ResourceFiles#config()
     */
    public List<Config> config() {
        return config;
    }

    /**
     * @return name resolver
     * @see ResourceFiles#namer()
     */
    public Namer namer() {
        return namer;
    }

    /**
     * Implementation of the
     * <a href="https://en.wikipedia.org/wiki/Builder_pattern">builder pattern</a>
     * for {@link Context}.
     */
    public static final class Builder {
        private ProcessingEnvironment env;
        private List<JavaFileManager.Location> locations;
        private Pkg pkg;
        private Element annotated;
        private List<Resource> resources;
        private List<Config> config;
        private Namer namer;

        Builder(Context ctxt) {
            this.env = ctxt.env;
            this.locations = ctxt.locations;
            this.pkg = ctxt.pkg;
            this.annotated = ctxt.annotated;
            this.resources = ctxt.resources;
            this.config = ctxt.config;
            this.namer = ctxt.namer;
        }

        Builder() {}

        /**
         * @return new context
         */
        public Context build() {
            return new Context(env, locations, pkg, annotated, resources, config, namer);
        }

        /**
         * @param annotated element
         * @return this
         * @see Context#annotated()
         */
        public Builder setAnnotated(Element annotated) {
            this.annotated = annotated;
            return this;
        }

        /**
         * @param config configuration options
         * @return this
         * @see Context#config()
         */
        public Builder setConfig(List<Config> config) {
            this.config = new ArrayList<>(config);
            return this;
        }

        /**
         * @param env processing environment
         * @return this
         * @see Context#env()
         */
        public Builder setEnv(ProcessingEnvironment env) {
            this.env = env;
            return this;
        }

        /**
         * @param locations resource location
         * @return this
         * @see Context#locations()
         */
        public Builder setLocation(List<JavaFileManager.Location> locations) {
            this.locations = new ArrayList<>(locations);
            return this;
        }

        /**
         * @param namer generated code namer
         * @return this
         * @see Context#namer()
         */
        public Builder setNamer(Namer namer) {
            this.namer = namer;
            return this;
        }

        /**
         * @param pkg annotated element package
         * @return this
         * @see Context#pkg()
         */
        public Builder setPkg(Pkg pkg) {
            this.pkg = pkg;
            return this;
        }

        /**
         * @param resources resource files
         * @return this
         * @see Context#resources()
         */
        public Builder setResources(List<Resource> resources) {
            this.resources = new ArrayList<>(resources);
            return this;
        }
    }
}
