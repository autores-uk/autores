package uk.autores.strong.test;

import org.junit.jupiter.api.Test;
import uk.autores.strong.DumpResourceToStdout;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DumpResourceToStdoutTest {

    @Test
    void run() throws IOException {
        DumpResourceToStdout.main();
    }
}
