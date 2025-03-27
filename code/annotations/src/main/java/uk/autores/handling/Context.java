// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;
import uk.autores.naming.Namer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import java.util.List;
import java.util.Optional;

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
        this.locations = locations;
        this.pkg = requireNonNull(pkg, "pkg");
        this.annotated = requireNonNull(annotated, "annotatedElement");
        this.resources = resources;
        this.config = config;
        this.namer = requireNonNull(namer, "namer");
    }

    /**
     * Builder pattern.
     *
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
     * Annotation processing environment.
     *
     * @return annotation processing environment
     */
    public ProcessingEnvironment env() {
        return env;
    }

    /**
     * Filer locations to search.
     *
     * @return where resources are to be loaded from
     * @see uk.autores.Processing#locations()
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
     * The annotated element.
     *
     * @return the annotated element - class or package
     * @see javax.lang.model.element.TypeElement
     * @see javax.lang.model.element.PackageElement
     */
    public Element annotated() {
        return annotated;
    }

    /**
     * Resources to process.
     *
     * @return unmodifiable resources list
     * @see ResourceFiles#value()
     */
    public List<Resource> resources() {
        return resources;
    }

    /**
     * Configuration.
     *
     * @return unmodifiable configuration list
     * @see ResourceFiles#config()
     */
    public List<Config> config() {
        return config;
    }

    /**
     * Naming strategy.
     *
     * @return name resolver
     * @see uk.autores.Processing#namer()
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
         * Creates immutable context instance.
         *
         * @return new context
         */
        public Context build() {
            return new Context(env, locations, pkg, annotated, resources, config, namer);
        }

        /**
         * Sets package or type.
         *
         * @param annotated element
         * @return this
         * @see Context#annotated()
         */
        public Builder setAnnotated(Element annotated) {
            this.annotated = annotated;
            return this;
        }

        /**
         * Sets configuration.
         *
         * @param config configuration options
         * @return this
         * @see Context#config()
         */
        public Builder setConfig(List<Config> config) {
            this.config = Lists.copy(config);
            return this;
        }

        /**
         * Sets processing environment.
         *
         * @param env processing environment
         * @return this
         * @see Context#env()
         */
        public Builder setEnv(ProcessingEnvironment env) {
            this.env = env;
            return this;
        }

        /**
         * Sets locations to search.
         *
         * @param locations resource location
         * @return this
         * @see Context#locations()
         */
        public Builder setLocation(List<JavaFileManager.Location> locations) {
            this.locations = Lists.copy(locations);
            return this;
        }

        /**
         * Sets naming conventions.
         *
         * @param namer generated code namer
         * @return this
         * @see Context#namer()
         */
        public Builder setNamer(Namer namer) {
            this.namer = namer;
            return this;
        }

        /**
         * Sets annotated package.
         *
         * @param pkg annotated element package
         * @return this
         * @see Context#pkg()
         */
        public Builder setPkg(Pkg pkg) {
            this.pkg = pkg;
            return this;
        }

        /**
         * Sets resources to process.
         *
         * @param resources resource files
         * @return this
         * @see Context#resources()
         */
        public Builder setResources(List<Resource> resources) {
            this.resources = Lists.copy(resources);
            return this;
        }
    }
}
