// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.consts.test;

import org.junit.jupiter.api.Test;
import uk.autores.consts.PrintCincoLobitos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PrintCincoLobitosTest {

    @Test
    void main() {
        PrintCincoLobitos.main();
    }

    @Test
    void constantsMatchProperties() throws UnsupportedEncodingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(buffer, true, StandardCharsets.UTF_8.name())) {
            Locale[] locales = {Locale.ROOT};
            String[] lines = PrintCincoLobitos.lines();

            PrintCincoLobitos.print(locales, lines, ps);
        }
        String result = buffer.toString(StandardCharsets.UTF_8.name());
        assertFalse(result.contains("null"));
    }
}
