# AUTORES BUILDS

## TOOLS

Building from source requires a [Java Development Kit](https://whichjdk.com/).
The minimum version is defined by the `maven.compiler.target` property in
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
