package uk.autores.custom.handler.test;

import org.junit.jupiter.api.Test;
import uk.autores.custom.handler.GenerateIconsFromFiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenerateIconsFromFilesTest {

    @Test
    void imageTemplateContext() {
        // keep spotbugs happy
        List<String> methods = Arrays.asList("foo", "bar", "baz");
        GenerateIconsFromFiles.ImageTemplateContext itc = new GenerateIconsFromFiles.ImageTemplateContext("foo", "bar", "baz", methods);
        assertNotNull(itc.toString());
    }
}