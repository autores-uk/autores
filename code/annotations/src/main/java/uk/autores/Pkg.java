package uk.autores;

import static java.util.Objects.requireNonNull;

/**
 * Package information.
 */
public final class Pkg {

    /** The fully qualified package name of the annotated element. */
    public final String name;

    private final boolean relative;

    public Pkg(String name, boolean relative) {
        this.name = requireNonNull(name, "name");
        this.relative = relative;
    }

    public String resourcePackage() {
        return relative ? name : "";
    }

    public String qualifiedClassName(String simpleClassName) {
        return name.isEmpty() ? simpleClassName : name + "." + simpleClassName;
    }

    public boolean isDefault() {
        return name.isEmpty();
    }
}
