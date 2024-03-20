// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.Processing;
import uk.autores.StringResources;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableStringResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class StringContexts extends ContextFactory<StringResources, RepeatableStringResources> {
    private final Handler handler = new GenerateStringsFromText();

    StringContexts(ProcessingEnvironment env) {
        super(env, StringResources.class, RepeatableStringResources.class);
    }

    @Override
    Handler handler(StringResources single) {
        return handler;
    }

    @Override
    List<Config> config(StringResources byteArrayResources) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrayResources.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrayResources.strategy().value()));
        cfg.add(new Config(CfgEncoding.ENCODING, byteArrayResources.encoding()));
        return cfg;
    }

    @Override
    StringResources[] expand(RepeatableStringResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(StringResources single) {
        return single.processing();
    }

    @Override
    String[] resources(StringResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(StringResources.class, RepeatableStringResources.class, StringContexts::new);
    }
}
