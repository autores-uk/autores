# AUTORES BUILDS

## TOOLS

Building from source requires a [Java Development Kit](https://sdkman.io/jdks).

 - Java Development Kit
   - e.g. [Temurin JDK](https://adoptium.net)
   - minimum version defined by `maven.compiler.target` property in pom

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
