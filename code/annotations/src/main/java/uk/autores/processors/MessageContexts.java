package uk.autores.processors;

import uk.autores.GenerateMessagesFromProperties;
import uk.autores.MessageResources;
import uk.autores.Processing;
import uk.autores.cfg.Format;
import uk.autores.cfg.Localize;
import uk.autores.cfg.MissingKey;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
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
            cfg.add(new Config(Visibility.VISIBILITY, Visibility.PUBLIC));
        }
        cfg.add(new Config(Format.FORMAT, m.format() ? Format.TRUE : Format.FALSE));
        cfg.add(new Config(Localize.LOCALIZE, m.localize() ? Localize.TRUE : Localize.FALSE));
        cfg.add(new Config(MissingKey.MISSING_KEY, m.missingKey().value()));

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
