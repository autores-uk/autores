// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.ByteArrayResources;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableByteArrayResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class ByteArrayContexts extends ContextFactory<ByteArrayResources, RepeatableByteArrayResources> {
    private final Handler handler = new GenerateByteArraysFromFiles();

    ByteArrayContexts(ProcessingEnvironment env) {
        super(env, ByteArrayResources.class, RepeatableByteArrayResources.class);
    }

    @Override
    Handler handler(ByteArrayResources single) {
        return handler;
    }

    @Override
    List<Config> config(ByteArrayResources byteArrayResources) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrayResources.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrayResources.strategy().value()));
        return cfg;
    }

    @Override
    ByteArrayResources[] expand(RepeatableByteArrayResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(ByteArrayResources single) {
        return single.processing();
    }

    @Override
    String[] resources(ByteArrayResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(ByteArrayResources.class, RepeatableByteArrayResources.class, ByteArrayContexts::new);
    }
}
