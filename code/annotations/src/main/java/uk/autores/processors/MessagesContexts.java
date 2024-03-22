// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

import uk.autores.Messages;
import uk.autores.Processing;
import uk.autores.handling.*;
import uk.autores.repeat.RepeatableMessageResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class MessagesContexts extends ContextFactory<Messages, RepeatableMessageResources> {
    private final Handler handler = new GenerateMessagesFromProperties();

    MessagesContexts(ProcessingEnvironment env) {
        super(env, Messages.class, RepeatableMessageResources.class);
    }

    @Override
    Handler handler(Messages single) {
        return handler;
    }

    @Override
    List<Config> config(Messages m) {
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
    Messages[] expand(RepeatableMessageResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(Messages single) {
        return single.processing();
    }

    @Override
    String[] resources(Messages single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(Messages.class, RepeatableMessageResources.class, MessagesContexts::new);
    }
}
