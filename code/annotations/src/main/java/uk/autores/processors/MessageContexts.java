// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.MessageResources;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableMessageResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class MessageContexts extends ContextFactory<MessageResources, RepeatableMessageResources> {
    private final Handler handler = new GenerateMessagesFromProperties();

    MessageContexts(ProcessingEnvironment env) {
        super(env, MessageResources.class, RepeatableMessageResources.class);
    }

    @Override
    Handler handler(MessageResources single) {
        return handler;
    }

    @Override
    List<Config> config(MessageResources m) {
        List<Config> cfg = new ArrayList<>();
        if (m.isPublic()) {
            cfg.add(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
        }
        cfg.add(new Config(CfgFormat.FORMAT, m.format() ? CfgFormat.TRUE : CfgFormat.FALSE));
        cfg.add(new Config(CfgLocalize.LOCALIZE, m.localize() ? CfgLocalize.TRUE : CfgLocalize.FALSE));
        cfg.add(new Config(CfgMissingKey.MISSING_KEY, m.missingKey().value()));

        return cfg;
    }

    @Override
    MessageResources[] expand(RepeatableMessageResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(MessageResources single) {
        return single.processing();
    }

    @Override
    String[] resources(MessageResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(MessageResources.class, RepeatableMessageResources.class, MessageContexts::new);
    }
}
