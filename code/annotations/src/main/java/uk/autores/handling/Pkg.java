// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

/**
 * Resource package information.
 */
public final class Pkg implements CharSequence {

    private static final Pkg UNNAMED = new Pkg("");

    /** The fully qualified package name of the annotated element. */
    private final CharSequence name;

    private Pkg(CharSequence name) {
        if (!Namer.isPackage(name)) {
            throw new AssertionError("Invalid name: " + name);
        }
        this.name = name;
    }

    /**
     * Factory method.
     *
     * @param name package name of the annotated type or package
     */
    public static Pkg named(CharSequence name) {
        return name.length() == 0
                ? UNNAMED
                : new Pkg(name);
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
        return isUnnamed() ? simpleClassName : name + "." + simpleClassName;
    }

    /**
     * NOTE: use of the unnamed package is highly discouraged.
     *
     * @return true if this is the unnamed package
     */
    public boolean isUnnamed() {
        return name.length() == 0;
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

    /**
     * For package "foo.bar" returns "bar".
     * For unnamed package returns "".
     *
     * @return the last dotted segment
     */
    public String lastSegment() {
        int idx = lastIndexOf('.') + 1;
        int start = Math.max(idx, 0);
        return name.subSequence(start, name.length()).toString();
    }

    private int lastIndexOf(char ch) {
        for (int i = name.length() - 1; i >= 0; i--) {
            if (name.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
