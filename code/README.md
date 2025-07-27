# AUTORES BUILDS

## TOOLS

Building from source requires [JDK17+](https://whichjdk.com/).

The required version is defined by the `maven.compiler.target` property in
[autores/pom.xml](autores/pom.xml).

## BUILD

Use the [Maven wrapper](https://maven.apache.org/wrapper/) scripts (`mvnw`/`mvnw.cmd`) to build:

```shell
./mvnw clean install
```

### OUTPUTS

Notable build artifacts:

 - `annotations/target/annotations-<version>.jar`: packaged binary
 - `annotations/target/apidocs/index.html`: API documentation
 - `annotations/target/site/jacoco/index.html`: test coverage
