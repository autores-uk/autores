package uk.autores.test.processors;

import uk.autores.ClasspathResource;
import uk.autores.GenerateByteArraysFromFiles;

@ClasspathResource(
        value = "BadConfig.java",
        handler = GenerateByteArraysFromFiles.class,
        config = {
                @ClasspathResource.Cfg(key = "foo", value = "bar"),
                @ClasspathResource.Cfg(key = "visibility", value = "bar"),
                @ClasspathResource.Cfg(key = "visibility", value = "public"),
        }
)
class BadConfig {
}
