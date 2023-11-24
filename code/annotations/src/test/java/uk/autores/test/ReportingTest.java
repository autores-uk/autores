// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.cfg.MissingKey;
import uk.autores.handling.*;
import uk.autores.test.env.TestElement;
import uk.autores.test.env.TestProcessingEnvironment;
import uk.autores.test.testing.Proxies;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportingTest {

    private final RProxy Reporting = Proxies.utility(RProxy.class, "uk.autores.Reporting");

    @Test
    void reportsErrorByDefault() {
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, Collections.emptyList());
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
                MissingKey.ERROR
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
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
                MissingKey.WARN
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
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
                MissingKey.IGNORE
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, MissingKey.DEF).accept(expected);
        // verify
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private Context ctxt(TestProcessingEnvironment env, List<Config> cfg) {
        return Context.builder()
                .setAnnotated(TestElement.INSTANCE)
                .setEnv(env)
                .setConfig(cfg)
                .setLocation(StandardLocation.CLASS_PATH)
                .setNamer(new Namer())
                .setPkg(Pkg.named(""))
                .setResources(Collections.emptyList())
                .build();
    }

    private interface RProxy {
        Consumer<String> reporter(Context context, ConfigDef def);
    }
}
