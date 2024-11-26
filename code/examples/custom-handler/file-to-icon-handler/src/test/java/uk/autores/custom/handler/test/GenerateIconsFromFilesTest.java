package uk.autores.custom.handler.test;

import org.junit.jupiter.api.Test;
import uk.autores.custom.handler.GenerateIconsFromFiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenerateIconsFromFilesTest {

    @Test
    void imageTemplateContext() {
        // keep spotbugs happy
        GenerateIconsFromFiles.ImageTemplateContext itc = new GenerateIconsFromFiles.ImageTemplateContext("foo", "bar", "baz");
        assertNotNull(itc.toString());
    }
}