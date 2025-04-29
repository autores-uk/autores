// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.ByteArrays;
import uk.autores.Processing;
import uk.autores.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.processing.handlers.CfgName;
import uk.autores.processing.handlers.CfgStrategy;
import uk.autores.processing.handlers.CfgVisibility;
import uk.autores.processing.handlers.GenerateByteArraysFromFiles;
import uk.autores.repeat.RepeatableByteArrays;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class ByteArraysContexts extends ContextFactory<ByteArrays, RepeatableByteArrays> {
    private final Handler handler = new GenerateByteArraysFromFiles();

    ByteArraysContexts(ProcessingEnvironment env) {
        super(env, ByteArrays.class, RepeatableByteArrays.class);
    }

    @Override
    Handler handler(ByteArrays single) {
        return handler;
    }

    @Override
    List<Config> config(ByteArrays byteArrays) {
        List<Config> cfg = new ArrayList<>();
        if (byteArrays.visibility() == Visibility.PUBLIC) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        String name = byteArrays.name();
        if (!"".equals(name)) {
            cfg.add(new Config(CfgName.NAME, name));
        }
        cfg.add(new Config(CfgStrategy.STRATEGY, byteArrays.strategy().token()));
        return cfg;
    }

    @Override
    ByteArrays[] expand(RepeatableByteArrays repeating) {
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
        return new AnnotationDef<>(ByteArrays.class, RepeatableByteArrays.class, ByteArraysContexts::new);
    }
}
