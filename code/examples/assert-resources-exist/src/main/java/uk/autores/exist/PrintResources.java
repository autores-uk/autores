package uk.autores.exist;

import uk.autores.ResourceFiles;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

@ResourceFiles({PrintResources.RELATIVE, PrintResources.ABSOLUTE})
public class PrintResources {

    // change these strings to see compilation failure
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
