package uk.autores;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

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
        checkBuffer();
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        for (int i = 0; i < csq.length(); i++) {
            buffer(csq.charAt(i));
        }
        checkBuffer();

        return this;
    }

    @Override
    public Writer append(char c) throws IOException {
        buffer(c);
        checkBuffer();

        return this;
    }

    private void buffer(char c) {
        if (c > 127) {
            b.append(String.format("\\u%04x", (int) c));
        } else {
            b.append(c);
        }
    }

    private void checkBuffer() throws IOException {
        if (b.length() >= 64 * 1024) {
            flushBuffer();
        }
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
        flush();
        w.close();
    }
}
