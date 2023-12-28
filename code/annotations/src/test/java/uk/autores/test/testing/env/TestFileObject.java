// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.testing.env;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class TestFileObject implements JavaFileObject {

    public final ByteVector data = new ByteVector();
    private final boolean exists;

    public TestFileObject(boolean exists) {
        this.exists = exists;
    }

    public TestFileObject() {
        this(false);
    }

    @Override
    public URI toUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (!exists) {
            throw new FileNotFoundException();
        }
        return data.in();
    }

    @Override
    public OutputStream openOutputStream() {
        return data.out();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }
}
