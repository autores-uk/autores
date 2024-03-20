// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.naming.Namer;

/**
 * Character representation of a
 * <a href="https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Java package</a>.
 */
public final class Pkg implements CharSequence {

    private static final Pkg UNNAMED = new Pkg("");

    /** The fully qualified package name of the annotated element. */
    private final CharSequence name;

    private Pkg(CharSequence name) {
        this.name = name;
    }

    /**
     * Instantiates an instance, decorating the given sequence.
     * The argument is typically a {@link javax.lang.model.element.Name}.
     *
     * @param name package name of the annotated type or package
     * @return instance
     */
    public static Pkg named(CharSequence name) {
        if (name.length() == 0) {
            return UNNAMED;
        }
        if (!Namer.isPackage(name)) {
            throw new AssertionError("Invalid name: " + name);
        }
        return new Pkg(name);
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
