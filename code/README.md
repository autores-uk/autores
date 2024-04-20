# AUTORES BUILDS

## TOOLS

Building from source requires [Maven](https://maven.apache.org) and a [Java Development Kit](https://sdkman.io/jdks).

 - Java Development Kit
   - e.g. [Temurin JDK](https://adoptium.net)
   - minimum version defined by `maven.compiler.target` property in pom
 - [Maven 3](https://maven.apache.org/users/index.html)
   - minimum version defined by `maven-enforcer-plugin` in pom

## BUILD

```shell
mvn clean install
```

### OUTPUTS

Notable build artifacts:

 - `annotations/target/annotations-<version>.jar`: packaged binary
 - `annotations/target/apidocs/index.html`: API documentation
 - `annotations/target/site/jacoco/index.html`: test coverage
