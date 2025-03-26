package uk.autores.test.processors;

import uk.autores.handling.GenerateByteArraysFromFiles;
import uk.autores.ResourceFiles;

@ResourceFiles(
        value = "BadConfig.java",
        handler = GenerateByteArraysFromFiles.class,
        config = {
                @ResourceFiles.Cfg(key = "foo", value = "bar"),
                @ResourceFiles.Cfg(key = "visibility", value = "bar"),
                @ResourceFiles.Cfg(key = "visibility", value = "public"),
        }
)
class BadConfig {
}
