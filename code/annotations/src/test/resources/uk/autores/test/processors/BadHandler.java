package uk.autores.test.processors;

import uk.autores.ClasspathResource;

@ClasspathResource(value = "won't get this far", handler = Foo.class)
class BadHandler {
}
