package uk.autores.test.processors;

import uk.autores.ClasspathResource;

@ClasspathResource("exists/resources/does_not_exist.txt")
class ClasspathResource_ERR_NOT_EXIST {
}
