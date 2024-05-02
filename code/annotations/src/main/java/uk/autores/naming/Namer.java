// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.naming;

/**
 * <p>
 *     Base type for naming classes, methods and fields.
 *     Does the bare minimum to create viable identifiers by replacing invalid codepoints with underscores.
 *     Extend this type to provide alternative behaviour and specify using {@link uk.autores.Processing#namer()}.
 * </p>
 * <p>
 *     Implementations MUST:
 * </p>
 * <ul>
 *     <li>Be public</li>
 *     <li>Have a public no-args constructor</li>
 *     <li>Be available as compiled types on the compiler classpath</li>
 *  </ul>
 *
 *      <p>Sample usage:</p>
 *      <pre>
 *          Namer namer = new Namer();
 *          String simple = namer.simplifyResourceName("META-INF/foo/bar.baz.txt"); // "foo.bar"
 *          String className = namer.nameClass(simple);                             // "bar_baz"
 *      </pre>
 */
public class Namer {

    private static final CharSequenceSet RESERVED = new CharSequenceSet(
            // keywords
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "goto", "package", "synchronized",
            "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while",
            // literals
            "true", "false", "null",
            // keyword Java 9 onwards
            "_"
    );

    /** Public no-args constructor as per contract. */
    public Namer() {}

    private static boolean isIdentifier(CharSequence cs, int off, int len) {
        if (len == 0 || RESERVED.contains(cs, off, len)) {
            return false;
        }
        // TODO: better codepoint handling
        if (!Character.isJavaIdentifierStart(cs.charAt(off))) {
            return false;
        }
        for (int i = off + 1, max = off + len; i < max; i++) {
            if (!Character.isJavaIdentifierPart(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests for valid
     * <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-Identifier">identifiers</a>
     *  - class, method or field names.
     *
     * @param cs sequence to test
     * @return true if valid identifier
     */
    public static boolean isIdentifier(CharSequence cs) {
        return isIdentifier(cs, 0, cs.length());
    }

    /**
     * Tests for valid <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html#jls-7.4.1">package names</a>.
     * Returns false for the empty string.
     *
     * @param pkg package name
     * @return true if valid package name
     */
    public static boolean isPackage(CharSequence pkg) {
        int off = 0;
        final int len = pkg.length();
        for (int i = 0; i < len; i++) {
            char ch = pkg.charAt(i);
            if (ch == '.') {
                if (!isIdentifier(pkg, off, i - off)) {
                    return false;
                }
                off = i + 1;
            }
        }
        return isIdentifier(pkg, off, len - off);
    }

    /**
     * Name a class/interface/etc.
     *
     * @param src source string
     * @return source string as a class name
     */
    public String nameType(String src) {
        return replace(src);
    }

    /**
     * Name a public static final field.
     *
     * @param src source string
     * @return source string as a field name if possible
     */
    public String nameConstant(String src) {
        return replace(src);
    }

    /**
     * Name a method/field/etc.
     *
     * @param src source string
     * @return source string as a member name
     */
    public String nameMember(String src) {
        return replace(src);
    }

    /**
     * Reduces a resource name to a simpler form.
     *
     * @param resource resource name
     * @return name stripped of path and extension
     */
    public String simplifyResourceName(String resource) {
        int begin = resource.lastIndexOf('/');
        if (begin < 0) {
            begin = 0;
        } else {
            begin++;
        }
        int end = resource.lastIndexOf('.');
        if (end < 0) {
            end = resource.length();
        }
        return resource.substring(begin, end);
    }

    private static String replace(String s) {
        if (isIdentifier(s)) {
            return s;
        }
        StringBuilder buf = new StringBuilder(s.length());
        for (int offset = 0; offset < s.length(); ) {
            final int codepoint = s.codePointAt(offset);
            add(buf, offset, codepoint);
            offset += Character.charCount(codepoint);
        }
        return buf.toString();
    }

    private static void add(StringBuilder buf, int offset, int codepoint) {
        if (offset == 0) {
            if (Character.isJavaIdentifierStart(codepoint)) {
                buf.append(Character.toChars(codepoint));
            } else {
                buf.append('_');
            }
            return;
        }

        if (Character.isJavaIdentifierPart(codepoint)) {
            buf.append(Character.toChars(codepoint));
        } else {
            buf.append('_');
        }
    }
}
