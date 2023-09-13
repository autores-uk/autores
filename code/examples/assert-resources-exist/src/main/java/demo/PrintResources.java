package demo;

import uk.autores.ClasspathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

@ClasspathResource(
        value = PrintResources.RELATIVE
)
@ClasspathResource(
        value = PrintResources.ABSOLUTE,
        relative = false
)
public class PrintResources {

    static final String RELATIVE = "relative.txt";
    static final String ABSOLUTE = "demo/absolute.txt";

    public static void main(String...args) throws IOException {
        String relative = load(PrintResources.class::getResourceAsStream, RELATIVE);
        System.out.println(relative);

        String absolute = load(PrintResources.class.getClassLoader()::getResourceAsStream, ABSOLUTE);
        System.out.println(absolute);
    }

    public static String load(ResourceAsStream ras, String resource) throws IOException {
        try (InputStream in = open(ras, resource);
             Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader buf = new BufferedReader(reader)) {
            return buf.readLine();
        }
    }

    private static InputStream open(ResourceAsStream ras, String resource) throws IOException {
        InputStream in = ras.open(resource);
        if (in == null) {
            throw new IOException("Not found: " + resource);
        }
        return in;
    }

    @FunctionalInterface
    private interface ResourceAsStream {
        InputStream open(String resource) throws IOException;
    }
}
