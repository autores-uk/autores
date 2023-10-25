package uk.autores.strong;

import com.google.common.io.ByteStreams;
import uk.autores.GenerateInputStreamsFromFiles;
import uk.autores.ResourceFiles;

import java.io.IOException;
import java.io.InputStream;

import static uk.autores.cfg.Name.NAME;

@ResourceFiles(
        value = "boiledeggs.txt",
        handler = GenerateInputStreamsFromFiles.class,
        config = @ResourceFiles.Cfg(key = NAME, value = "Food")
)
public class DumpResourceToStdout {

    public static void main(String... args) throws IOException {
        try (InputStream eggs = Food.boiledeggs()) {
            ByteStreams.copy(eggs, System.out);
        }
    }
}
