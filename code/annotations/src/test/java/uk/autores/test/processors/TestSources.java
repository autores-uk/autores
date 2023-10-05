package uk.autores.test.processors;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class TestSources {

    static Source load(Object relativeTo, String name) throws IOException {
        URL url = relativeTo.getClass().getResource(name);
        String sourceCode = read(url);

        String packName = relativeTo.getClass().getPackage().getName();
        String simpleName = name.substring(0, name.length() - ".java".length());
        String className = packName + "." + simpleName;

        return new Source(className, sourceCode);
    }

    private static String read(URL url) throws IOException {
        StringWriter writer = new StringWriter();
        try (InputStream in = url.openStream();
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            reader.transferTo(writer);
        }
        return writer.toString();
    }

    static final class Source {
        final String className;
        final String sourceCode;

        Source(String className, String sourceCode) {
            this.className = className;
            this.sourceCode = sourceCode;
        }
    }
}
