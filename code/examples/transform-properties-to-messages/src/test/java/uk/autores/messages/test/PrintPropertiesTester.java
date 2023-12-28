// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages.test;

import uk.autores.messages.MessagePrinter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PrintPropertiesTester {

    private PrintPropertiesTester() {}

    private static <MP extends MessagePrinter> List<String> run(BiConsumer<PrintStream, MP> action, Supplier<MP> supplier) {
        MP mp = supplier.get();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(buffer, true, "UTF-8");
            action.accept(ps, mp);
            String result = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
            return new BufferedReader(new StringReader(result))
                    .lines()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static <MP extends MessagePrinter> void assertOutput(Supplier<MP> supplier, BiConsumer<PrintStream, MP> action, String... expected) {
        List<String> actual = run(action, supplier);
        List<String> e = Arrays.asList(expected);
        assertEquals(e, actual);
    }
}
