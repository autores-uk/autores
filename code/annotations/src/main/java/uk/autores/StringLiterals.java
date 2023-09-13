package uk.autores;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/** Methods for escaping special String characters. */
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

    public static void write(Reader r, Writer w) throws IOException {
        while (true) {
            int ch = r.read();
            if (ch < 0) {
                break;
            }
            write(w, (char) ch);
        }
    }
}
