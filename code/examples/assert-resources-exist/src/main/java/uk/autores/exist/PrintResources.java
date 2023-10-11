package uk.autores.exist;

import uk.autores.ClasspathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

@ClasspathResource(
        value = PrintResources.RELATIVE
)
@ClasspathResource(
        value = PrintResources.ABSOLUTE,
        relative = false
)
public class PrintResources {

    static final String RELATIVE = "relative.txt";
    static final String ABSOLUTE = "/uk/autores/exist/absolute.txt";

    public static void main(String...args) throws IOException {
        String relative = load(RELATIVE);
        System.out.println(relative);

        String absolute = load(ABSOLUTE);
        System.out.println(absolute);
    }

    private static String load(String resource) throws IOException {
        try (InputStream in = requireNonNull(PrintResources.class.getResourceAsStream(resource), resource);
             Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader buf = new BufferedReader(reader)) {
            return buf.readLine();
        }
    }
}
