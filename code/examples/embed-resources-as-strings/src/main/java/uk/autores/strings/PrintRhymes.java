// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.strings;

import uk.autores.Texts;

// UTF-8 encoded text files containing nursery rhymes
@Texts(value = {"Poule.txt", "Roses.txt"}, isPublic = true)
public class PrintRhymes {

    public static void main(String...args)  {
        // Just access text directly
        System.out.println(Poule.text());
        System.out.println(Roses.text());
    }
}
