package uk.autores.env;

import uk.autores.processing.Resource;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ResourceSets {

    private ResourceSets() {}

    private static Resource make(TestProcessingEnvironment env, String name, String seed, int multiplier) throws IOException {
        String data = IntStream.range(0, multiplier).mapToObj(i -> seed).collect(Collectors.joining());
        TestFileObject text = new TestFileObject(true);
        try (OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(name, text);
        return new Resource(text, name);
    }

    public static SortedSet<Resource> largeAndSmallTextFile(TestProcessingEnvironment env, int multiplier) throws IOException {
        SortedSet<Resource> files = new TreeSet<>();
        files.add(make(env, "small.txt", "abc", 1));
        files.add(make(env, "large.txt", "abc", multiplier));
        return files;
    }

    public static SortedSet<Resource> infinitelyLargeFile(TestProcessingEnvironment env) {
        String filename = "infinite.txt";
        TestInfiniteFileObject infinite = new TestInfiniteFileObject();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, infinite);
        SortedSet<Resource> files = new TreeSet<>();
        files.add(new Resource(infinite, filename));
        return files;
    }

    public static SortedSet<Resource> junkWithBadFilename(TestProcessingEnvironment env, String filename) throws IOException {
        SortedSet<Resource> files = new TreeSet<>();
        files.add(make(env, filename, "abc", 1));
        return files;
    }

    public static SortedSet<Resource> of(TestProcessingEnvironment env, String filename, TestFileObject file) {
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);
        SortedSet<Resource> files = new TreeSet<>();
        files.add(new Resource(file, filename));
        return files;
    }
}
