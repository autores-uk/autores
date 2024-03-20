// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.KeyedResources;
import uk.autores.Processing;
import uk.autores.handling.CfgVisibility;
import uk.autores.handling.Config;
import uk.autores.handling.GenerateConstantsFromProperties;
import uk.autores.handling.Handler;
import uk.autores.repeat.RepeatableKeyedResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class KeyedContexts extends ContextFactory<KeyedResources, RepeatableKeyedResources> {
    private final Handler handler = new GenerateConstantsFromProperties();

    KeyedContexts(ProcessingEnvironment env) {
        super(env, KeyedResources.class, RepeatableKeyedResources.class);
    }

    @Override
    Handler handler(KeyedResources single) {
        return handler;
    }

    @Override
    List<Config> config(KeyedResources m) {
        List<Config> cfg = new ArrayList<>();
        if (m.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }

        return cfg;
    }

    @Override
    KeyedResources[] expand(RepeatableKeyedResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(KeyedResources single) {
        return single.processing();
    }

    @Override
    String[] resources(KeyedResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(KeyedResources.class, RepeatableKeyedResources.class, KeyedContexts::new);
    }
}
