package uk.autores.env;

import java.io.*;

/** WARNING: reading never finishes */
public class TestInfiniteFileObject extends TestFileObject {

    @Override
    public InputStream openInputStream() throws IOException {
        class InfiniteInputStream extends InputStream {

            @Override
            public int read() throws IOException {
                return 0;
            }
        }

        return new InfiniteInputStream();
    }

}
