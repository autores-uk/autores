package uk.autores;

import java.io.IOException;
import java.io.Reader;

/** char buffer that ensures it does not exceed a certain size when encoded as UTF-8. */
final class Utf8Buffer implements CharSequence {

    private final char[] cbuf;
    private int length = 0;
    private int utf8Length = 0;
    private final int maxUtf8Length;

    private Utf8Buffer(int size) {
        // Code units for every code point
        // https://www.unicode.org/Public/15.1.0/ucd/UnicodeData.txt
        // utf16=1 utf8=1 =134
        // utf16=1 utf8=2 =1863
        // utf16=2 utf8=4 =18034
        // utf16=1 utf8=3 =14900

        cbuf = new char[size * 2];
        maxUtf8Length = size;
    }

    /**
     * Reads chars into a buffer but limits the number of chars to a UTF-8 byte length.
     * {@link Reader} must provide valid UTF-16 sequences and {@link Reader#markSupported()}
     * must return true.
     *
     * @param reader buffered reader
     * @return true if data has been read; false otherwise
     * @throws IOException on I/O error
     */
    boolean receive(Reader reader) throws IOException {
        length = 0;
        utf8Length = 0;

        reader.mark(cbuf.length);
        int r = reader.read(cbuf);
        if (r < 0) {
            return false;
        }
        if (r == 0) {
            return true;
        }

        for (int i = 0; i < r; i++) {
            char ch = cbuf[i];
            int codePoint;
            int utf6Len;
            if (Character.isSurrogate(ch)) {
                throwMalformed(r == 1);
                if (i == r - 1) {
                    // don't split surrogates
                    break;
                }
                char low = cbuf[++i];
                throwMalformed(!Character.isSurrogatePair(ch, low));
                codePoint = Character.toCodePoint(ch, low);
                utf6Len = 2;
            } else {
                codePoint = ch;
                utf6Len = 1;
            }
            int bytes = utf8Bytes(codePoint);
            if (bytes + utf8Length > maxUtf8Length) {
                break;
            }
            utf8Length += bytes;
            length += utf6Len;
        }

        reader.reset();
        r = reader.read(cbuf, 0, length);
        assert r == length;

        return true;
    }

    private static int utf8Bytes(int codePoint) {
        // https://datatracker.ietf.org/doc/html/rfc3629
        return codePoint <= 0x7F ? 1
                : codePoint <= 0x7FF ? 2
                : codePoint <= 0xFFFF ? 3
                : 4;
    }

    private void throwMalformed(boolean predicate) throws IOException {
        if (predicate) {
            throw new IOException("Malformed surrogates");
        }
    }

    @Override
    public int length() {
        return length;
    }

    int utf8Length() {
        return utf8Length;
    }

    @Override
    public char charAt(int index) {
        outOfBounds(index < 0 || index >= length);
        return cbuf[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int count = end - start;
        outOfBounds(start < 0 || end < start || count > length);
        return String.valueOf(cbuf, start, count);
    }

    @Override
    public String toString() {
        return String.valueOf(cbuf, 0, length);
    }

    private void outOfBounds(boolean predicate) {
        if (predicate) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * @param maxUtf8Length must be at least four bytes
     * @return the buffer
     */
    static Utf8Buffer size(int maxUtf8Length) {
        assert maxUtf8Length >= 4;

        return new Utf8Buffer(maxUtf8Length);
    }
}
