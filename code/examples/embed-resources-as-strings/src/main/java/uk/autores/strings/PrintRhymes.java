// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strings;

import uk.autores.GenerateStringsFromText;
import uk.autores.ResourceFiles;
import uk.autores.cfg.Strategy;
import uk.autores.cfg.Visibility;

import static uk.autores.cfg.Strategy.STRATEGY;
import static uk.autores.cfg.Visibility.VISIBILITY;

@ResourceFiles(
        value = {
                "Poule.txt",
                "Roses.txt",
        },
        handler = GenerateStringsFromText.class,
        config = {
                @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.INLINE),
                @ResourceFiles.Cfg(key = VISIBILITY, value = Visibility.PUBLIC),
        }
)
public class PrintRhymes {

    public static void main(String...args)  {
        System.out.println(Poule.text());
        System.out.println(Roses.text());
    }
}
