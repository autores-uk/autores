// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.env;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestFiler implements Filer {

    public final Map<JavaFileManager.Location, Map<String, TestFileObject>> files = new HashMap<>();

    public TestFiler() {
        Stream.of(StandardLocation.values()).forEach(sl -> files.put(sl, new HashMap<>()));
    }

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) throws IOException {
        if (files.containsKey(name)) {
            throw new IOException("Already exists: " + name);
        }
        // this isn't very safe - allows over-write where real env wouldn't
        TestFileObject file = new TestFileObject(false);
        Map<String, TestFileObject> map = files.computeIfAbsent(StandardLocation.SOURCE_OUTPUT, k -> new HashMap<>());
        map.put(name.toString(), file);
        return file;
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element... originatingElements) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public FileObject createResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName, Element... originatingElements) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public FileObject getResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName) throws IOException {
        Map<String, TestFileObject> map = files.get(location);
        String path = (pkg.length() == 0) ? relativeName.toString() : pkg.toString().replace('.', '/') + '/' + relativeName;
        return map.getOrDefault(path, new TestFileObject());
    }
}
