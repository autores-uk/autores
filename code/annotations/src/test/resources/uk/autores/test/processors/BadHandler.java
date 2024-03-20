package uk.autores.test.processors;

import uk.autores.handling.ResourceFiles;

@ResourceFiles(value = "won't get this far", handler = Foo.class)
class BadHandler {
}
