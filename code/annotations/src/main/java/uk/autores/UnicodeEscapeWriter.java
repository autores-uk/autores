package uk.autores;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/** Writes non-ASCII chars as Unicode escape sequences. */
final class UnicodeEscapeWriter extends Writer {

    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private final char[] formatBuffer = "\\u0000".toCharArray();

    private final Writer w;
    private final StringBuilder b = new StringBuilder();
    boolean closed = false;

    UnicodeEscapeWriter(Writer w) {
        this.w = Objects.requireNonNull(w);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            buffer(cbuf[i]);
        }
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        for (int i = 0, len = csq.length(); i < len; i++) {
            buffer(csq.charAt(i));
        }
        return this;
    }

    @Override
    public Writer append(char c) throws IOException {
        buffer(c);
        return this;
    }

    private void buffer(char c) throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }

        if (escape(c)) {
            b.append(format(c));
        } else {
            b.append(c);
        }

        if (b.length() >= 64 * 1024) {
            flushBuffer();
        }
    }

    private boolean escape(char ch) {
        if (ch > '~') {
            // Outside ASCII range except DEL which will be escaped
            return true;
        }
        if (ch >= ' ') {
            // Visible ASCII
            return false;
        }
        // Allow select control characters as visible whitespace
        return ch != '\n' && ch != '\r' && ch != '\t';
    }

    private char[] format(char c) {
        formatBuffer[5] = HEX[c & 0xF];
        formatBuffer[4] = HEX[(c >>> 4) & 0xF];
        formatBuffer[3] = HEX[(c >>> 8) & 0xF];
        formatBuffer[2] = HEX[(c >>> 12) & 0xF];
        return formatBuffer;
    }

    private void flushBuffer() throws IOException {
        try {
            w.append(b);
        } finally {
            b.delete(0, b.length());
        }
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        w.flush();
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;
        try {
            flush();
        } finally {
            w.close();
        }
    }
}
