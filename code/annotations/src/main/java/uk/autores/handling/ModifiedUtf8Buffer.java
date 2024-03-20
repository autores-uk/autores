// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import java.io.IOException;
import java.io.Reader;

/**
 * <code>char</code> buffer that ensures it does not exceed a certain size when encoded as Modified UTF-8.
 * This specialized buffer is for generating string literals in classes.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7
 */
final class ModifiedUtf8Buffer implements CharSequence {

    /**
     * String literals must fit into the constant pool encoded as UTF-8.
     * See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7">u4 code_length</a>.
     * String literals of exactly 0xFFFF length fail as too long on some compilers.
     */
    static final int CONST_BYTE_LIMIT = 0xFFFF - 1;

    private final char[] cbuf;
    private int length = 0;
    private int utf8Length = 0;
    private final int maxUtf8Length;

    ModifiedUtf8Buffer(int maxUtf8Length) {
        assert maxUtf8Length >= 3;

        cbuf = new char[maxUtf8Length];
        this.maxUtf8Length = maxUtf8Length;
    }

    ModifiedUtf8Buffer() {
        this(CONST_BYTE_LIMIT);
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

        for (int i = 0; i < r; i++) {
            char ch = cbuf[i];
            int bytes = byteLen(ch);
            if (bytes + utf8Length > maxUtf8Length) {
                break;
            }
            utf8Length += bytes;
            length ++;
        }

        reader.reset();
        r = reader.read(cbuf, 0, length);
        assert r == length;

        return true;
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

    public int maxBuffer() {
        return cbuf.length;
    }

    private void outOfBounds(boolean predicate) {
        if (predicate) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * In Java's modified UTF-8 scheme all chars are measured individually.
     *
     * @param ch candidate
     * @return number of bytes for this char
     */
    static int byteLen(char ch) {
        if (ch == '\u0000') {
            return 2;
        } else if (ch < '\u0080') {
            return 1;
        } else if (ch < '\u0800') {
            return 2;
        }
        return 3;
    }
}
