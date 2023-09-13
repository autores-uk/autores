package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestProcessingEnvironment;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportingTest {

    @Test
    void reportsErrorByDefault() {
        List<Config> empty = new ArrayList<>();
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                new TreeMap<>(),
                empty,
                new Namer()
        );
        String expected = "default";
        // invoke
        Reporting.reporter(context, ConfigDefs.MISSING_KEY).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void errorCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                ConfigDefs.MISSING_KEY.name(),
                "error"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                new TreeMap<>(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, ConfigDefs.MISSING_KEY).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void warnCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                ConfigDefs.MISSING_KEY.name(),
                "warn"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                new TreeMap<>(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, ConfigDefs.MISSING_KEY).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.WARNING).get(0));
    }

    @Test
    void errorsCanBeIgnored() {
        List<Config> cfg = singletonList(new Config(
                ConfigDefs.MISSING_KEY.name(),
                "ignore"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                new TreeMap<>(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, ConfigDefs.MISSING_KEY).accept(expected);
        // verify
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }
}