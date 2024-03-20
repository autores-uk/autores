// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.naming;

import java.util.Locale;
import java.util.function.Function;

// TODO: look at codepoint handling

/**
 * Generates names that conform to Java naming conventions.
 * This type can generate more name collisions than the base type.
 * Case handling uses {@link Locale#ENGLISH}.
 */
public final class IdiomaticNamer extends Namer {

    /** Public no-args constructor as per contract. */
    public IdiomaticNamer() {}

    /**
     * For input "foo-bar-baz" returns "fooBarBaz".
     *
     * @param src source string
     * @return lower snake case
     */
    @Override
    public String nameMember(String src) {
        String name = super.nameMember(src);
        return transform(name, new MemberTransform(), "");
    }

    /**
     * For input "foo-bar-baz" returns "FOO_BAR_BAZ".
     *
     * @param src source string
     * @return upper snake case
     */
    @Override
    public String nameConstant(String src) {
        String name = super.nameConstant(src);
        return transform(name, IdiomaticNamer::allUpper, "_");
    }

    /**
     * For input "foo-bar-baz" returns "FooBarBaz".
     *
     * @param src source string
     * @return upper camel case
     */
    @Override
    public String nameType(String src) {
        String name = super.nameType(src);
        return transform(name, IdiomaticNamer::firstUpper, "");
    }

    private String transform(String src, Function<String, String> transform, String delim) {
        StringBuilder buf = new StringBuilder(src.length());
        char last = '!';
        int offset = 0;
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch == '_') {
                segment(src, transform, buf, delim, offset, i);
                offset = i + 1;
            } else if(splitCondition(last, ch))  {
                segment(src, transform, buf, delim, offset, i);
                offset = i;
            }
            last = ch;
        }
        segment(src, transform, buf, delim, offset, src.length());
        return buf.toString();
    }

    private void segment(String src, Function<String, String> transform, StringBuilder buf, String delim, int start, int end) {
        if (start != end) {
            String segment = src.substring(start, end);
            segment = transform.apply(segment);
            if (buf.length() != 0) {
                buf.append(delim);
            }
            buf.append(segment);
        }
    }

    private boolean splitCondition(char last, char current) {
        return switchFromLowerToUpper(last, current) || switchFromDigitToNonDigit(last, current);
    }

    private boolean switchFromLowerToUpper(char last, char current) {
        return Character.isLowerCase(last) && Character.isUpperCase(current);
    }

    private boolean switchFromDigitToNonDigit(char last, char current) {
        return Character.isDigit(last) && !Character.isDigit(current);
    }

    private static String firstUpper(String s) {
        char first = s.charAt(0);
        first = Character.toUpperCase(first);
        String rest = s.substring(1).toLowerCase(Locale.ENGLISH);
        return first + rest;
    }

    private static String allLower(String s) {
        return s.toLowerCase(Locale.ENGLISH);
    }

    private static String allUpper(String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }

    private static final class MemberTransform implements Function<String, String> {
        private boolean first = true;
        @Override
        public String apply(String s) {
            if (first) {
                first = false;
                return allLower(s);
            }
            return firstUpper(s);
        }
    }
}
