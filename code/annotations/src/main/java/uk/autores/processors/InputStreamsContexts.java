// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.InputStreams;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableInputStreams;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class InputStreamsContexts extends ContextFactory<InputStreams, RepeatableInputStreams> {
    private final Handler handler = new GenerateInputStreamsFromFiles();

    InputStreamsContexts(ProcessingEnvironment env) {
        super(env, InputStreams.class, RepeatableInputStreams.class);
    }

    @Override
    Handler handler(InputStreams single) {
        return handler;
    }

    @Override
    List<Config> config(InputStreams r) {
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
    InputStreams[] expand(RepeatableInputStreams repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(InputStreams single) {
        return single.processing();
    }

    @Override
    String[] resources(InputStreams single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(InputStreams.class, RepeatableInputStreams.class, InputStreamsContexts::new);
    }
}
