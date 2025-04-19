// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing;

import uk.autores.Messages;
import uk.autores.Processing;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.processing.handlers.*;
import uk.autores.repeat.RepeatableMessages;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class MessagesContexts extends ContextFactory<Messages, RepeatableMessages> {
    private final Handler handler = new GenerateMessagesFromProperties();

    MessagesContexts(ProcessingEnvironment env) {
        super(env, Messages.class, RepeatableMessages.class);
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
        cfg.add(new Config(CfgIncompatibleFormat.INCOMPATIBLE_FORMAT, m.incompatibleFormat().value()));

        return cfg;
    }

    @Override
    Messages[] expand(RepeatableMessages repeating) {
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
        return new AnnotationDef<>(Messages.class, RepeatableMessages.class, MessagesContexts::new);
    }
}
