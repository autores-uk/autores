# AUTORES EXAMPLES

These projects both act as examples of how to use the Autores
library and form part of the test corpus.

Generated code can be inspected in built projects by
navigating to the `target/generated-sources` directories.

| Example                           | Handler                               | Notes                                                                    |
|-----------------------------------|---------------------------------------|--------------------------------------------------------------------------|
| assert-resources-exist            | AssertResourceExists.class            | See also for non-relative paths                                          |
| custom-handler                    | custom                                | How to write your own handler                                            |
| embed-resources-as-bytes          | GenerateByteArraysFromFiles.class     |                                                                          |
| embed-resources-as-strings        | GenerateStringsFromText.class         | Note the inline strategy and exclusion of resource files via the pom.xml |
| transform-properties-to-constants | GenerateConstantsFromProperties.class |                                                                          |
| transform-properties-to-messages  | GenerateMessagesFromProperties.class  | See also for configuring options                                         |

