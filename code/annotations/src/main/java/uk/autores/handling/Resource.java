package uk.autores.handling;

import uk.autores.ResourceFiles;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A representation of the annotated resource.
 */
public final class Resource implements CharSequence {

    private final FileObject file;
    private final String path;

    /**
     * @param file file
     * @param path as defined in {@link ResourceFiles#value()}
     */
    public Resource(FileObject file, String path) {
        this.file = Objects.requireNonNull(file, "file");
        this.path = Objects.requireNonNull(path, "path");
    }

    /**
     * @return contents
     * @throws IOException on I/O error
     */
    public InputStream open() throws IOException {
        return file.openInputStream();
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

    @Override
    public String toString() {
        return path;
    }
}
