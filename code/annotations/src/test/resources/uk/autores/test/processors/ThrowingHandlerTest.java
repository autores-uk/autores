package uk.autores.test.processors;

import uk.autores.ClasspathResource;
import uk.autores.test.processors.ThrowingHandler;

@ClasspathResource(value = "ThrowingHandlerTest.java", handler= ThrowingHandler.class)
class ThrowingHandlerTest {
}
