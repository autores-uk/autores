package uk.autores.env;

import java.io.*;

/** WARNING: reading never finishes */
public class TestInfiniteFileObject extends TestFileObject {

    @Override
    public InputStream openInputStream() throws IOException {
        class InfiniteInputStream extends InputStream {

            @Override
            public int read() {
                return 0;
            }

            @Override
            public int read(byte[] b) {
                return b.length;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                return len;
            }
        }

        return new InfiniteInputStream();
    }

}
