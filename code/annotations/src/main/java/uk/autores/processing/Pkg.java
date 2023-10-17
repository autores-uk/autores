package uk.autores.processing;

import static java.util.Objects.requireNonNull;

/**
 * Resource package information.
 */
public final class Pkg implements CharSequence {

    /** The fully qualified package name of the annotated element. */
    private final String name;

    /**
     * @param name package name of the annotated type or package
     */
    public Pkg(String name) {
        this.name = requireNonNull(name, "name");
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

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    public String lastSegment() {
        int idx = name.lastIndexOf('.') + 1;
        int start = Math.max(idx, 0);
        return name.substring(start);
    }

    @Override
    public String toString() {
        return name;
    }
}
