// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Config;
import uk.autores.handling.Context;
import uk.autores.handling.Pkg;
import uk.autores.naming.Namer;
import uk.autores.processing.testing.env.TestElement;
import uk.autores.processing.testing.env.TestProcessingEnvironment;

import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportingTest {

    @Test
    void reportsErrorByDefault() {
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, Collections.emptyList());
        String expected = "default";
        // invoke
        Reporting.reporter(context, CfgMissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void errorCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                CfgMissingKey.DEF.key(),
                CfgMissingKey.ERROR
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, CfgMissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.ERROR).get(0));
    }

    @Test
    void warnCanBeConfigured() {
        List<Config> cfg = singletonList(new Config(
                CfgMissingKey.DEF.key(),
                CfgMissingKey.WARN
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, CfgMissingKey.DEF).accept(expected);
        // verify
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(expected, env.getMessager().messages.get(Diagnostic.Kind.WARNING).get(0));
    }

    @Test
    void errorsCanBeIgnored() {
        List<Config> cfg = singletonList(new Config(
                CfgMissingKey.DEF.key(),
                CfgMissingKey.IGNORE
        ));
        // setup
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context context = ctxt(env, cfg);
        String expected = "foo bar baz";
        // invoke
        Reporting.reporter(context, CfgMissingKey.DEF).accept(expected);
        // verify
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }

    private Context ctxt(TestProcessingEnvironment env, List<Config> cfg) {
        return Context.builder()
                .setAnnotated(TestElement.INSTANCE)
                .setEnv(env)
                .setConfig(cfg)
                .setLocation(singletonList(StandardLocation.CLASS_PATH))
                .setNamer(new Namer())
                .setPkg(Pkg.named(""))
                .setResources(Collections.emptyList())
                .build();
    }
}
