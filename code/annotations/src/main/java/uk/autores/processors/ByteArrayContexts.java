// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.ByteArrayResources;
import uk.autores.GenerateByteArraysFromFiles;
import uk.autores.Processing;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
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
            cfg.add(new Config(Visibility.VISIBILITY, Visibility.PUBLIC));
        }
        cfg.add(new Config(Strategy.STRATEGY, byteArrayResources.strategy().value()));
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
