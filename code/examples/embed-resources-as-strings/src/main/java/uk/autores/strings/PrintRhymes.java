// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strings;

import uk.autores.handling.CfgStrategy;
import uk.autores.handling.CfgVisibility;
import uk.autores.handling.GenerateStringsFromText;
import uk.autores.handling.ResourceFiles;

import static uk.autores.handling.CfgStrategy.STRATEGY;
import static uk.autores.handling.CfgVisibility.VISIBILITY;

@ResourceFiles(
        value = {
                "Poule.txt",
                "Roses.txt",
        },
        handler = GenerateStringsFromText.class,
        config = {
                @ResourceFiles.Cfg(key = STRATEGY, value = CfgStrategy.INLINE),
                @ResourceFiles.Cfg(key = VISIBILITY, value = CfgVisibility.PUBLIC),
        }
)
public class PrintRhymes {

    public static void main(String...args)  {
        System.out.println(Poule.text());
        System.out.println(Roses.text());
    }
}
