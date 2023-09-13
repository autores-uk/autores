package uk.autores.processors;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class TestSources {

    static Source load(Object relativeTo, String name) throws IOException {
        URL url = Resources.getResource(relativeTo.getClass(), name);
        CharSource cs = Resources.asCharSource(url, StandardCharsets.UTF_8);
        String sourceCode = cs.read();

        String packName = relativeTo.getClass().getPackage().getName();
        String simpleName = name.substring(0, name.length() - ".java".length());
        String className = packName + "." + simpleName;

        return new Source(className, sourceCode);
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
