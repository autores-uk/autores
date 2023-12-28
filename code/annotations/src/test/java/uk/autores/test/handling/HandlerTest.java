// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.Config;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Context;
import uk.autores.handling.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

class HandlerTest {

    private final Handler test = context -> {};

    @Test
    void config() {
        assertTrue(test.config().isEmpty());
    }

    @Test
    void passesValidConfig() {
        List<String> errs = new ArrayList<>();
        boolean valid = test.validConfig(emptyList(), errs::add);
        assertTrue(valid);
        assertTrue(errs.isEmpty());
    }

    @Test
    void detectsUnsupportedConfig() {
        List<String> errs = new ArrayList<>();
        boolean valid = test.validConfig(singletonList(new Config("foo", "bar")), errs::add);
        assertFalse(valid);
        assertEquals(1, errs.size());
    }

    @Test
    void detectsDuplicateConfigKeys() {
        Handler handlerWithNoRepeatingConfig = new Handler() {
            @Override
            public void handle(Context context) {}

            @Override
            public Set<ConfigDef> config() {
                return singleton(new ConfigDef("foo", s -> true));
            }
        };
        List<String> errs = new ArrayList<>();
        boolean valid = handlerWithNoRepeatingConfig.validConfig(asList(new Config("foo", "bar"), new Config("foo", "baz")), errs::add);
        assertFalse(valid);
        assertEquals(1, errs.size());
    }

    @Test
    void detectsInvalidValue() {
        Handler noValidationHandler = new Handler() {
            @Override
            public void handle(Context context) {}

            @Override
            public Set<ConfigDef> config() {
                return singleton(new ConfigDef("foo", s -> false));
            }
        };
        List<String> errs = new ArrayList<>();
        boolean valid = noValidationHandler.validConfig(singletonList(new Config("foo", "bar")), errs::add);
        assertFalse(valid);
        assertEquals(1, errs.size());
    }
}
