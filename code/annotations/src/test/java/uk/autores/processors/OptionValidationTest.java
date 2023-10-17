package uk.autores.processors;

import org.junit.jupiter.api.Test;
import uk.autores.AssertResourceExists;
import uk.autores.env.TestElement;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.*;

import javax.tools.StandardLocation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionValidationTest {

    @Test
    void passesValidConfig() {
        Handler handler = new AssertResourceExists();
        Context context = context(emptyList());

        boolean valid = OptionValidation.areValid(handler, context);
        assertTrue(valid);
    }

    @Test
    void validationCanBeDisabled() {
        class NoValidationHandler implements Handler {
            @Override
            public void handle(Context context) {}

            @Override
            public boolean validateConfig() {
                return false;
            }
        }
        Context context = context(singletonList(new Config("foo", "bar")));

        boolean valid = OptionValidation.areValid(new NoValidationHandler(), context);
        assertTrue(valid);
    }

    @Test
    void detectsUnsupportedConfig() {
        Handler handlerWithNoConfig = ConfigurableHandler.with();
        Context context = context(singletonList(new Config("foo", "bar")));

        boolean valid = OptionValidation.areValid(handlerWithNoConfig, context);
        assertFalse(valid);
    }

    @Test
    void detectsDuplicateConfigKeys() {
        boolean repeatable = false;
        ConfigDef foo = new ConfigDef("foo", s -> true, "");
        Handler handlerWithNoRepeatingConfig = ConfigurableHandler.with(foo);

        Context context = context(asList(new Config("foo", "bar"), new Config("foo", "baz")));

        boolean valid = OptionValidation.areValid(handlerWithNoRepeatingConfig, context);
        assertFalse(valid);
    }

    @Test
    void detectsInvalidValue() {
        Predicate<String> allWrong = s -> false;
        ConfigDef foo = new ConfigDef("foo", allWrong, "");

        class NoValidationHandler implements Handler {
            @Override
            public void handle(Context context) {}

            @Override
            public Set<ConfigDef> config() {
                return singleton(foo);
            }
        }
        Context context = context(singletonList(new Config("foo", "bar")));

        boolean valid = OptionValidation.areValid(new NoValidationHandler(), context);
        assertFalse(valid);
    }

    private Context context(List<Config> config) {
        return new Context(
                new TestProcessingEnvironment(),
                StandardLocation.CLASS_PATH,
                new Pkg(""),
                TestElement.INSTANCE,
                emptyList(),
                config,
                new Namer()
        );
    }

    private static class ConfigurableHandler implements Handler {

        private final Set<ConfigDef> defs;

        private ConfigurableHandler(Set<ConfigDef> defs) {
            this.defs = defs;
        }

        @Override
        public void handle(Context context) throws Exception {
        }

        @Override
        public Set<ConfigDef> config() {
            return defs;
        }

        static Handler with(ConfigDef...defs) {
            Set<ConfigDef> set = new HashSet<>(asList(defs));
            return new ConfigurableHandler(set);
        }
    }
}
