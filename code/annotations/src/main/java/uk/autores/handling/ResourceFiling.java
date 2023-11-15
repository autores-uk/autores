package uk.autores.handling;

import javax.tools.JavaFileManager;
import java.util.Objects;

/**
 * Utility type for working with {@link javax.annotation.processing.Filer}.
 * Sample usage:
 * <pre><code>
 *     Filer filer = ...;
 *     String annotationPackage = "foo.bar";
 *     String resourcePath = "/META-INF/foo.txt";
 *     String pkg = ResourceFiling.pkg(annotationPackage, resourcePath);
 *     String relativeName = ResourceFiling.relativeName(resourcePath);
 *     FileObject fo = filer.getResource(location, pkg, relativeName);
 * </code></pre>
 */
public final class ResourceFiling {

    private ResourceFiling() {}

    /**
     * Resolves the package string to be passed to
     * {@link javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     *
     * @param annotationPackage the annotated package
     * @param resourcePath non-empty resource path
     * @return the package to use in the filer
     */
    public static CharSequence pkg(CharSequence annotationPackage, CharSequence resourcePath) {
        boolean absolute = resourcePath.charAt(0) == '/';
        return absolute ? "" : Objects.requireNonNull(annotationPackage, "pkg");
    }

    /**
     * Resolves the relative name to be passed to
     * {@link javax.annotation.processing.Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}.
     *
     * @param resourcePath non-empty resource path as specified in annotation
     * @return the resource name to use in the filer
     */
    public static CharSequence relativeName(CharSequence resourcePath) {
        boolean absolute = resourcePath.charAt(0) == '/';
        return absolute ? resourcePath.subSequence(1, resourcePath.length()) : resourcePath;
    }
}
