package uk.autores.test.processors;

import uk.autores.ResourceFiles;

@ResourceFiles(value = "won't get this far", handler = Foo.class)
class BadHandler {
}
