// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.env;

import java.io.IOException;
import java.io.InputStream;

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
