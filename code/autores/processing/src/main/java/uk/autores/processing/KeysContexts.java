// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.Keys;
import uk.autores.Processing;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.processing.handlers.CfgVisibility;
import uk.autores.processing.handlers.GenerateConstantsFromProperties;
import uk.autores.repeat.RepeatableKeys;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class KeysContexts extends ContextFactory<Keys, RepeatableKeys> {
    private final Handler handler = new GenerateConstantsFromProperties();

    KeysContexts(ProcessingEnvironment env) {
        super(env, Keys.class, RepeatableKeys.class);
    }

    @Override
    Handler handler(Keys single) {
        return handler;
    }

    @Override
    List<Config> config(Keys m) {
        List<Config> cfg = new ArrayList<>();
        if (m.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }

        return cfg;
    }

    @Override
    Keys[] expand(RepeatableKeys repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(Keys single) {
        return single.processing();
    }

    @Override
    String[] resources(Keys single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(Keys.class, RepeatableKeys.class, KeysContexts::new);
    }
}
