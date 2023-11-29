// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.testing;

import uk.autores.handling.Resource;
import uk.autores.test.env.TestFileObject;
import uk.autores.test.env.TestInfiniteFileObject;
import uk.autores.test.env.TestProcessingEnvironment;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class ResourceSets {

    private ResourceSets() {}

    private static Resource make(TestProcessingEnvironment env, String name, String seed, int multiplier) throws IOException {
        String data = IntStream.range(0, multiplier).mapToObj(i -> seed).collect(Collectors.joining());
        TestFileObject text = new TestFileObject(true);
        try (OutputStream out = text.openOutputStream()) {
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(name, text);
        return new Resource(text::openInputStream, name);
    }

    public static List<Resource> largeAndSmallTextFile(TestProcessingEnvironment env, int multiplier) throws IOException {
        List<Resource> files = new ArrayList<>();
        files.add(make(env, "small.txt", "abc", 1));
        files.add(make(env, "large.txt", "abc", multiplier));
        return files;
    }

    public static List<Resource> infinitelyLargeFile(TestProcessingEnvironment env) {
        String filename = "infinite.txt";
        TestInfiniteFileObject infinite = new TestInfiniteFileObject();
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, infinite);
        List<Resource> files = new ArrayList<>();
        files.add(new Resource(infinite::openInputStream, filename));
        return files;
    }

    public static List<Resource> junkWithBadFilename(TestProcessingEnvironment env, String filename) throws IOException {
        List<Resource> files = new ArrayList<>();
        files.add(make(env, filename, "abc", 1));
        return files;
    }

    public static List<Resource> of(TestProcessingEnvironment env, String filename, TestFileObject file) {
        env.getFiler().files.get(StandardLocation.CLASS_PATH).put(filename, file);
        List<Resource> files = new ArrayList<>();
        files.add(new Resource(file::openInputStream, filename));
        return files;
    }
}
