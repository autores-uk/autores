package uk.autores.processors;

import uk.autores.Processing;
import uk.autores.ResourceFiles;
import uk.autores.handling.Config;
import uk.autores.handling.Handler;
import uk.autores.repeat.RepeatableResourceFiles;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

public class ResourceContexts extends ContextFactory<ResourceFiles, RepeatableResourceFiles> {
    ResourceContexts(ProcessingEnvironment env) {
        super(env, ResourceFiles.class, RepeatableResourceFiles.class);
    }

    @Override
    Handler handler(ResourceFiles single) {
        return instance(single::handler);
    }

    @Override
    List<Config> config(ResourceFiles resourceFiles) {
        List<Config> configs = new ArrayList<>();
        for (ResourceFiles.Cfg c : resourceFiles.config()) {
            configs.add(new Config(c.key(), c.value()));
        }
        return configs;
    }

    @Override
    ResourceFiles[] expand(RepeatableResourceFiles repeating) {
        return repeating.value();
    }

    @Override
    Processing processing(ResourceFiles single) {
        return single.processing();
    }

    @Override
    String[] resources(ResourceFiles single) {
        return single.value();
    }

    static AnnotationDef<?, ?> def() {
        return new AnnotationDef<>(ResourceFiles.class, RepeatableResourceFiles.class, ResourceContexts::new);
    }
}
