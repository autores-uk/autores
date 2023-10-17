package uk.autores.strong;

import com.google.common.io.ByteStreams;
import uk.autores.GenerateInputStreamsFromFiles;
import uk.autores.ResourceFiles;

import java.io.IOException;
import java.io.InputStream;

@ResourceFiles(value = "boiledeggs.txt", handler = GenerateInputStreamsFromFiles.class)
public class DumpResourceToStdout {

    public static void main(String... args) throws IOException {
        try (InputStream eggs = strong.boiledeggs()) {
            ByteStreams.copy(eggs, System.out);
        }
    }
}
