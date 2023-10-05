package uk.autores.test.env;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteVector {

    private byte[] arr = {};
    private int length;

    private void cap(int capacity) {
        if (capacity > arr.length) {
            int suggested = arr.length + 8 * 1024;
            int size = suggested > capacity ? suggested : capacity;
            byte[] next = new byte[size];
            System.arraycopy(arr, 0, next, 0, length);
            arr = next;
        }
    }

    public OutputStream out() {
        length = 0;

        class Out extends OutputStream {

            @Override
            public void write(int b) throws IOException {
                cap(length + 1);
                arr[length++] = (byte) b;
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                cap(len + length);
                System.arraycopy(b, off, arr, length, len);
                length += len;
            }
        }

        return new Out();
    }

    public InputStream in() {
        return new ByteArrayInputStream(arr, 0, length);
    }

    public byte[] toByteArray() {
        byte[] copy = new byte[length];
        System.arraycopy(arr, 0, copy, 0, length);
        return copy;
    }
}
