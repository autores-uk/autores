// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strong;

import com.google.common.io.ByteStreams;
import uk.autores.InputStreamResources;

import java.io.IOException;
import java.io.InputStream;

@InputStreamResources(
        value = {"Charles-E-Weller.txt", "etaoin-shrdlu.txt", "Lorem-ipsum.txt"},
        name = "FillerText"
)
public class DumpFillerTextToStdout {

    public static void main(String... args) throws IOException {
        // InputStreams from generated class file
        try (InputStream weller = FillerText.charlesEWeller();
            InputStream etaoinShrdlu = FillerText.etaoinShrdlu();
            InputStream loremIpsum = FillerText.loremIpsum()) {

            ByteStreams.copy(weller, System.out);
            ByteStreams.copy(etaoinShrdlu, System.out);
            ByteStreams.copy(loremIpsum, System.out);
        }
    }
}
