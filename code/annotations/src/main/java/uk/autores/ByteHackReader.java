package uk.autores;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import static java.util.Objects.requireNonNull;

/**
 * Specialized {@link Reader} for putting bytes into <code>char</code> arrays.
 * Each pair of <code>byte</code>s becomes one <code>char</code>.
 * @see #lastByteOdd() for byte streams that are odd numbers
 */
final class ByteHackReader extends Reader {

    private final InputStream in;
    private int oddByte = -1;
    private boolean closed = false;

    ByteHackReader(final InputStream in) {
        this.in = requireNonNull(in);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = 0;
        for (int i = off, x = off + len; i < x; i++) {
            int r = read();
            if (r < 0) {
                break;
            }
            cbuf[i] = (char) r;
            count++;
        }

        return count == 0 ? -1 : count;
    }

    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }

        int high = in.read();
        if (high < 0) {
            return high;
        }
        int low = in.read();
        if (low < 0) {
            oddByte = high;
            return low;
        }
        return (high << 8) | low;
    }

    /**
     * A <code>char</code> is two <code>byte</code>s.
     * Need to handle odd stream lengths.
     *
     * @return true if the stream had an odd byte
     */
    public boolean lastByteOdd() {
        return oddByte != -1;
    }

    public byte getOddByte() {
        if (!closed) {
            throw new AssertionError();
        }
        return (byte) oddByte;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;
        in.close();
    }
}
