// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strong;

import com.google.common.io.ByteStreams;
import uk.autores.GenerateInputStreamsFromFiles;
import uk.autores.ResourceFiles;

import java.io.IOException;
import java.io.InputStream;

import static uk.autores.cfg.Name.NAME;

@ResourceFiles(
        value = {"Charles-E-Weller.txt", "etaoin-shrdlu.txt", "Lorem-ipsum.txt"},
        handler = GenerateInputStreamsFromFiles.class,
        config = @ResourceFiles.Cfg(key = NAME, value = "FillerText")
)
public class DumpFillerTextToStdout {

    public static void main(String... args) throws IOException {
        // InputStreams from generated class file
        try (InputStream weller = FillerText.Charles_E_Weller();
            InputStream etaoinShrdlu = FillerText.etaoin_shrdlu();
            InputStream loremIpsum = FillerText.Lorem_ipsum()) {

            ByteStreams.copy(weller, System.out);
            ByteStreams.copy(etaoinShrdlu, System.out);
            ByteStreams.copy(loremIpsum, System.out);
        }
    }
}
