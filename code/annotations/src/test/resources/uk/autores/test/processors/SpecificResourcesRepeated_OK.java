package uk.autores.test.processors;

import uk.autores.*;

@ByteArrayResources(isPublic = true)
@ByteArrayResources(value = "SpecificResourcesRepeated_OK.java", strategy = Strat.INLINE)
@InputStreamResources(isPublic = true)
@InputStreamResources(name = "Foo")
@MessageResources(isPublic = true, format = false, localize = false)
@MessageResources
@KeyedResources(isPublic = true)
@KeyedResources
@StringResources(isPublic = true)
@StringResources
class SpecificResourcesRepeated_OK {
}
