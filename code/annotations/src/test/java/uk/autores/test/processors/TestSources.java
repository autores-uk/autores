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

    private static void transferTo(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        while (true) {
            int r = reader.read(buf);
            if (r < 0) {
                break;
            }
            writer.write(buf, 0, r);
        }
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
