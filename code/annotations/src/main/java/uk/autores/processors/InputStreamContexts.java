// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.InputStreamResources;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableInputStreamResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class InputStreamContexts extends ContextFactory<InputStreamResources, RepeatableInputStreamResources> {
    private final Handler handler = new GenerateInputStreamsFromFiles();

    InputStreamContexts(ProcessingEnvironment env) {
        super(env, InputStreamResources.class, RepeatableInputStreamResources.class);
    }

    @Override
    Handler handler(InputStreamResources single) {
        return handler;
    }

    @Override
    List<Config> config(InputStreamResources r) {
        List<Config> cfg = new ArrayList<>();
        if (r.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        if (!r.name().isEmpty()) {
            cfg.add(new Config(CfgName.NAME, r.name()));
        }
        return cfg;
    }

    @Override
    InputStreamResources[] expand(RepeatableInputStreamResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(InputStreamResources single) {
        return single.processing();
    }

    @Override
    String[] resources(InputStreamResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(InputStreamResources.class, RepeatableInputStreamResources.class, InputStreamContexts::new);
    }
}
