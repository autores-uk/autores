// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.Processing;
import uk.autores.ResourceFiles;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.repeat.RepeatableResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class ResourceContexts extends ContextFactory<ResourceFiles, RepeatableResources> {
    ResourceContexts(ProcessingEnvironment env) {
        super(env, ResourceFiles.class, RepeatableResources.class);
    }

    @Override
    Handler handler(ResourceFiles single) {
        return instance(single::handler);
    }

    @Override
    List<Config> config(ResourceFiles resourceFiles) {
        var configs = new ArrayList<Config>();
        for (ResourceFiles.Cfg c : resourceFiles.config()) {
            configs.add(new Config(c.key(), c.value()));
        }
        return configs;
    }

    @Override
    ResourceFiles[] expand(RepeatableResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(ResourceFiles single) {
        return single.processing();
    }

    @Override
    String[] resources(ResourceFiles single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(ResourceFiles.class, RepeatableResources.class, ResourceContexts::new);
    }
}
