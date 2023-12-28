// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.messages;

import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Locale;

public interface MessagePrinter {

    void print(PrintStream out, Locale l, ZonedDateTime time);
}
