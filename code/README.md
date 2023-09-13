# AUTORES BUILDS

## TOOLS

Building from source requires [Maven](https://maven.apache.org) and a [Java Development Kit](https://sdkman.io/jdks).

 - Java Development Kit of choice - version 8 or above
   - e.g. [Temurin JDK](https://adoptium.net)
 - [Maven 3.x.x](https://maven.apache.org/users/index.html)

## BUILD

```shell
mvn clean install
```

### OUTPUTS

Notable build artifacts:

 - `annotations/target/annotations-<version>.jar`: packaged binary
 - `annotations/target/apidocs/index.html`: API documentation
 - `annotations/target/site/jacoco/index.html`: test coverage
