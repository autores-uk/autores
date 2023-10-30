package uk.autores;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/** Writes non-ASCII chars as Unicode escape sequences. */
final class UnicodeEscapeWriter extends Writer {

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

        if (escaped(c)) {
            b.append(String.format("\\u%04x", (int) c));
        } else {
            b.append(c);
        }

        if (b.length() >= 64 * 1024) {
            flushBuffer();
        }
    }

    private boolean escaped(char ch) {
        if (ch > '~') {
            return true;
        }
        if (ch >= ' ') {
            return false;
        }
        return ch != '\n' && ch != '\r' && ch != '\t';
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
