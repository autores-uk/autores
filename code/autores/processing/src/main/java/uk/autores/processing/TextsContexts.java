// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.Processing;
import uk.autores.Texts;
import uk.autores.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.processing.handlers.*;
import uk.autores.repeat.RepeatableTexts;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class TextsContexts extends ContextFactory<Texts, RepeatableTexts> {
    private final Handler handler = new GenerateStringsFromText();

    TextsContexts(ProcessingEnvironment env) {
        super(env, Texts.class, RepeatableTexts.class);
    }

    @Override
    Handler handler(Texts single) {
        return handler;
    }

    @Override
    List<Config> config(Texts byteArrayResources) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrayResources.visibility() == Visibility.PUBLIC) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        String name = byteArrayResources.name();
        if (!"".equals(name)) {
            cfg.add(new Config(CfgName.NAME, name));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrayResources.strategy().token()));
        cfg.add(new Config(CfgEncoding.ENCODING, byteArrayResources.encoding()));
        return cfg;
    }

    @Override
    Texts[] expand(RepeatableTexts repeating) {
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
        return new AnnotationDef<>(Texts.class, RepeatableTexts.class, TextsContexts::new);
    }
}
