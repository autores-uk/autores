package uk.autores.env;

import java.io.*;

public class TestMassiveFileObject extends TestFileObject {

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
