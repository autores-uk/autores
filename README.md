# AutoRes.uk

THIS PROJECT IS IN ALPHA STATE.

Annotation driven Java code generation library for working with embedded resources.

## See

 - [Documentation](https://autores.uk)
 - [Examples](code/examples/)
 - [Build](code/) with Maven
 - [Core source code](code/annotations/)

## License

Apache Software License 2.0.

No restrictions are placed on library consumers on licensing of code generated by this library.

## Contributions

Pull requests will NOT be accepted.
[Defect reports](https://github.com/autores-uk/autores/issues) are welcome but may not be acted upon.

## Branches

 - dev8: Primary development branch targeting Java 8
 - dev11: Java 11 fork with native module support
 - main: Most recent release for Java 11
 - release/*: released versions

| Branch | CI                                                                                                       |
| ------ | -------------------------------------------------------------------------------------------------------- |
| dev8   | ![ci.yaml](https://github.com/autores-uk/autores/actions/workflows/ci.yaml/badge.svg?branch=dev8)        |
| dev11  | ![ci.yaml](https://github.com/autores-uk/autores/actions/workflows/ci.yaml/badge.svg?branch=dev11)       |
| main   | ![ci.yaml](https://github.com/autores-uk/autores/actions/workflows/ci.yaml/badge.svg?branch=main) ![docs.yaml](https://github.com/autores-uk/autores/actions/workflows/docs.yaml/badge.svg?branch=main) |

## Releases

[Bytecode, source code, and javadoc](https://s01.oss.sonatype.org/content/repositories/releases/uk/autores/annotations/)
 are published to the
[Maven central repository](https://central.sonatype.com/artifact/uk.autores/annotations).

## Tests

See [autores-integration](https://github.com/autores-uk/autores-integration) for additional examples.
