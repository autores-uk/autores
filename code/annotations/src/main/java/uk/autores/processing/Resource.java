package uk.autores.processing;

import uk.autores.ResourceFiles;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A representation of the annotated resource.
 */
public final class Resource implements Comparable<Resource> {

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

    /**
     * @return as {@link ResourceFiles#value()}
     */
    public String path() {
        return path;
    }

    /**
     * As required by {@link javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     *
     * @return as {@link #path()} but without a leading slash
     */
    public String filerPath() {
        return path.charAt(0) == '/'
                ? path.substring(1)
                : path;
    }

    /**
     * Compares {@link #path()}.
     *
     * @param o other
     * @return result
     */
    @Override
    public int compareTo(Resource o) {
        return path.compareTo(o.path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != Resource.class) {
            return false;
        }
        Resource other = (Resource) obj;
        return other.path.equals(path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    /**
     * @return {@link #path()}
     */
    @Override
    public String toString() {
        return path;
    }
}
