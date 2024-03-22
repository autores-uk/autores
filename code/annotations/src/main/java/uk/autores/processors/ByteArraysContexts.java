// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.ByteArrays;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableByteArrayResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class ByteArraysContexts extends ContextFactory<ByteArrays, RepeatableByteArrayResources> {
    private final Handler handler = new GenerateByteArraysFromFiles();

    ByteArraysContexts(ProcessingEnvironment env) {
        super(env, ByteArrays.class, RepeatableByteArrayResources.class);
    }

    @Override
    Handler handler(ByteArrays single) {
        return handler;
    }

    @Override
    List<Config> config(ByteArrays byteArrays) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrays.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrays.strategy().value()));
        return cfg;
    }

    @Override
    ByteArrays[] expand(RepeatableByteArrayResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(ByteArrays single) {
        return single.processing();
    }

    @Override
    String[] resources(ByteArrays single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(ByteArrays.class, RepeatableByteArrayResources.class, ByteArraysContexts::new);
    }
}
