package uk.autores.handling;

import uk.autores.ResourceFiles;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * <p>
 *     Utilized in naming classes, methods and fields.
 *     Does the bare minimum to create viable identifiers by replacing invalid codepoints with underscores.
 *     Extend this type to provide alternative behaviour and specify using {@link ResourceFiles#namer()}.
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

    private static final Set<String> RESERVED = new HashSet<>(asList(
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
    ));

    /**
     * Validates class, method or field names.
     * A Java identifier is considered valid when:
     * <ul>
     *     <li>It has length &gt; 0</li>
     *     <li>It is not a reserved word</li>
     *     <li>The first codepoint satisfies {@link Character#isJavaIdentifierStart(int)}</li>
     *     <li>All codepoints satisfy {@link Character#isJavaIdentifierPart(int)}</li>
     * </ul>
     *
     * @param s the string to test
     * @return true if this is a valid identifier
     */
    public static boolean isIdentifier(String s) {
        if ("".equals(s) || RESERVED.contains(s)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
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
     * Name a public static field.
     *
     * @param src source string
     * @return source string as a field name if possible
     */
    public String nameStaticField(String src) {
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
        if (Namer.isIdentifier(s)) {
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
