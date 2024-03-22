package uk.autores.test.processors;

import uk.autores.*;

@ByteArrays(isPublic = true)
@ByteArrays(value = "SpecificResourcesRepeated_OK.java", strategy = Strategy.INLINE)
@InputStreams(isPublic = true)
@InputStreams(name = "Foo")
@Messages(isPublic = true, format = false, localize = false)
@Messages
@Keys(isPublic = true)
@Keys
@Texts(isPublic = true)
@Texts
class SpecificResourcesRepeated_OK {
}
