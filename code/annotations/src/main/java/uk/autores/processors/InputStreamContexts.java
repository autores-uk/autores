package uk.autores.processors;

import uk.autores.GenerateInputStreamsFromFiles;
import uk.autores.InputStreamResources;
import uk.autores.Processing;
import uk.autores.cfg.Name;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.repeat.RepeatableInputStreamResources;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

final class InputStreamContexts extends ContextFactory<InputStreamResources, RepeatableInputStreamResources> {
    private final Handler handler = new GenerateInputStreamsFromFiles();

    InputStreamContexts(ProcessingEnvironment env) {
        super(env, InputStreamResources.class, RepeatableInputStreamResources.class);
    }

    @Override
    Handler handler(InputStreamResources single) {
        return handler;
    }

    @Override
    List<Config> config(InputStreamResources r) {
        List<Config> cfg = new ArrayList<>();
        if (r.isPublic()) {
            cfg.add(new Config(Visibility.VISIBILITY, Visibility.PUBLIC));
        }
        if (!r.name().isEmpty()) {
            cfg.add(new Config(Name.NAME, r.name()));
        }
        return cfg;
    }

    @Override
    InputStreamResources[] expand(RepeatableInputStreamResources repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(InputStreamResources single) {
        return single.processing();
    }

    @Override
    String[] resources(InputStreamResources single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(InputStreamResources.class, RepeatableInputStreamResources.class, InputStreamContexts::new);
    }
}
