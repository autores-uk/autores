package uk.autores;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.MissingKey;
import uk.autores.env.TestElement;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.Config;
import uk.autores.processing.Context;
import uk.autores.processing.Namer;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportingTest {

    @Test
    void reportsErrorByDefault() {
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                emptyList(),
                emptyList(),
                new Namer()
        );
        String expected = "default";
        // invoke
        Reporting.reporter(context, MissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void errorCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                MissingKey.DEF.key(),
                "error"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                emptyList(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, MissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void warnCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                MissingKey.DEF.key(),
                "warn"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                emptyList(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, MissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.WARNING).get(0));
    }

    @Test
    void errorsCanBeIgnored() {
        List<Config> cfg = singletonList(new Config(
                MissingKey.DEF.key(),
                "ignore"
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = new Context(
                env,
                StandardLocation.CLASS_PATH,
                TestPkgs.P,
                TestElement.INSTANCE,
                emptyList(),
                cfg,
                new Namer()
        );
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, MissingKey.DEF).accept(expected);
        // verify
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }
}
