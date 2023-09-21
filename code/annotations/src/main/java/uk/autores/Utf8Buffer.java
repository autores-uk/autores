package uk.autores;

import java.io.IOException;
import java.io.Reader;

/** char buffer that ensures it does not exceed a certain size when encoded as UTF-8. */
final class Utf8Buffer implements CharSequence {

    private final char[] cbuf;
    private int length = 0;
    private int utf8Length = 0;

    private Utf8Buffer(int size) {
        cbuf = new char[size];
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

        for (; length < cbuf.length; length++) {
            reader.mark(2);
            int n = reader.read();
            if (n < 0) {
                reader.reset();
                break;
            }
            cbuf[length] = (char) n;
            int codePoint;
            boolean pair = false;
            if (Character.isHighSurrogate(cbuf[length])) {
                if (length == cbuf.length - 1) {
                    reader.reset();
                    break;
                }
                n = reader.read();
                if (n < 0) {
                    throw new IOException("EOF - broken surrogate pair");
                }
                cbuf[length + 1] = (char) n;
                codePoint = Character.toCodePoint(cbuf[length], cbuf[length + 1]);
                pair = true;
            } else {
                codePoint = cbuf[length];
            }
            int numbytes = utf8Bytes(codePoint);
            if (utf8Length + numbytes > cbuf.length) {
                reader.reset();
                break;
            }
            if (pair) {
                length++;
            }
            utf8Length += numbytes;
        }

        return length != 0;
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

    private static int utf8Bytes(int codePoint) {
        // https://datatracker.ietf.org/doc/html/rfc3629
        return codePoint <= 0x7F ? 1
                : codePoint <= 0x7FF ? 2
                : codePoint <= 0xFFFF ? 3
                : 4;
    }

    static Utf8Buffer size(int maxUtf8Length) {
        assert maxUtf8Length >= 4;

        return new Utf8Buffer(maxUtf8Length);
    }
}
