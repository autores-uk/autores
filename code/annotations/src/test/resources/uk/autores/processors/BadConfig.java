package uk.autores.processors;

import uk.autores.ResourceFiles;
import uk.autores.GenerateByteArraysFromFiles;

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
