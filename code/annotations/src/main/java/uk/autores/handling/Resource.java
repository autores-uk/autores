// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.ResourceFiles;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A representation of the annotated resource.
 * The {@link CharSequence} implementation is the path.
 */
public final class Resource implements CharSequence {

    private final ResourceOpener file;
    private final String path;

    /**
     * @param file resource bytes
     * @param path as defined in {@link ResourceFiles#value()}
     */
    public Resource(ResourceOpener file, String path) {
        this.file = Objects.requireNonNull(file, "file");
        this.path = Objects.requireNonNull(path, "path");
    }

    /**
     * Opens resource for reading.
     *
     * @return contents
     * @throws IOException on I/O error
     * @see FileObject#openInputStream()
     */
    public InputStream open() throws IOException {
        return file.open();
    }

    @Override
    public int length() {
        return path.length();
    }

    @Override
    public char charAt(int index) {
        return path.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return path.subSequence(start, end);
    }

    /**
     * @return path passed to the constructor
     */
    @Override
    public String toString() {
        return path;
    }

    /**
     * For retrieving resource contents.
     */
    @FunctionalInterface
    public interface ResourceOpener {
        /**
         * Opens resource for reading.
         *
         * @return resource bytes
         * @throws IOException on error
         */
        InputStream open() throws IOException;
    }
}
