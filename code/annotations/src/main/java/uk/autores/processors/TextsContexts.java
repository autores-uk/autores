// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.Processing;
import uk.autores.Texts;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableStringResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class TextsContexts extends ContextFactory<Texts, RepeatableStringResources> {
    private final Handler handler = new GenerateStringsFromText();

    TextsContexts(ProcessingEnvironment env) {
        super(env, Texts.class, RepeatableStringResources.class);
    }

    @Override
    Handler handler(Texts single) {
        return handler;
    }

    @Override
    List<Config> config(Texts byteArrayResources) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrayResources.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrayResources.strategy().value()));
        cfg.add(new Config(CfgEncoding.ENCODING, byteArrayResources.encoding()));
        return cfg;
    }

    @Override
    Texts[] expand(RepeatableStringResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(Texts single) {
        return single.processing();
    }

    @Override
    String[] resources(Texts single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(Texts.class, RepeatableStringResources.class, TextsContexts::new);
    }
}
