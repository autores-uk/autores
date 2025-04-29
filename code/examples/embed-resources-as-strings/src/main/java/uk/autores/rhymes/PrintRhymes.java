// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.rhymes;

import uk.autores.Texts;

import static uk.autores.Visibility.PUBLIC;

// UTF-8 encoded text files containing nursery rhymes
@Texts(value = {"Poule.txt", "Roses.txt"}, visibility = PUBLIC)
public class PrintRhymes {

    public static void main(String...args)  {
        // Just access text directly
        System.out.println(Rhymes.poule());
        System.out.println(Rhymes.roses());
    }
}
