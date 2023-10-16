package uk.autores.processors;

import uk.autores.GenerateByteArraysFromFiles;
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
