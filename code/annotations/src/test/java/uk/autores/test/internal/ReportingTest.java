package uk.autores.test.internal;

import org.junit.jupiter.api.Test;
import uk.autores.ConfigDefs;
import uk.autores.internal.Reporting;
import uk.autores.processing.Config;
import uk.autores.processing.Context;
import uk.autores.processing.Namer;
import uk.autores.test.env.TestElement;
import uk.autores.test.env.TestPkgs;
import uk.autores.test.env.TestProcessingEnvironment;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
                new TreeSet<>(),
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
                new TreeSet<>(),
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
                new TreeSet<>(),
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
                new TreeSet<>(),
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