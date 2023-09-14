package uk.autores.processing;

import static java.util.Objects.requireNonNull;

/**
 * Package information.
 */
public final class Pkg {

    /** The fully qualified package name of the annotated element. */
    public final String name;

    private final boolean relative;

    /**
     * @param name package name of the annotated type or package
     * @param relative if the resource path is relative to the package
     * @see Package#getName()
     * @see uk.autores.ClasspathResource#relative()
     */
    public Pkg(String name, boolean relative) {
        this.name = requireNonNull(name, "name");
        this.relative = relative;
    }

    /**
     * @return the package name or the empty string if this package is relative
     */
    public String resourcePackage() {
        return relative ? name : "";
    }

    /**
     * For a package "foo.bar" and a simple name "Baz" returns "foo.bar.Baz".
     * For unnamed package and simple name "Foo" returns "Foo".
     *
     * @param simpleClassName simple class name without package
     * @return the fully qualified class name
     * @see Class#getSimpleName()
     */
    public String qualifiedClassName(String simpleClassName) {
        return name.isEmpty() ? simpleClassName : name + "." + simpleClassName;
    }

    /**
     * NOTE: use of the unnamed package is highly discouraged.
     *
     * @return true if this is the unnamed package
     */
    public boolean isUnnamed() {
        return name.isEmpty();
    }
}
