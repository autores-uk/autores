// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import java.io.IOException;
import java.io.Writer;

/** Methods for escaping special characters in string literals. */
final class StringLiterals {

    private static final String[] ESCS = generateEscapes();

    private StringLiterals() {}

    private static String[] generateEscapes() {
        // backslash is last escape char
        int end = '\\' + 1;
        String[] arr = new String[end];
        for (int i = 0; i < end; i++) {
            arr[i] = String.valueOf((char) i);
        }
        arr['\t'] = "\\t";
        arr['\b'] = "\\b";
        arr['\n'] = "\\n";
        arr['\r'] = "\\r";
        arr['\f'] = "\\f";
        arr['"'] = "\\\"";
        arr['\\'] = "\\\\";
        return arr;
    }

    private static void write(Writer w, char ch) throws IOException {
        if (ch < ESCS.length) {
            w.append(ESCS[ch]);
        } else {
            w.append(ch);
        }
    }

    public static void write(CharSequence cs, Writer w) throws IOException {
        for (int i = 0, len = cs.length(); i < len; i++) {
            write(w, cs.charAt(i));
        }
    }
}
