// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.consts;

import uk.autores.KeyedResources;

import java.io.PrintStream;
import java.util.Locale;
import java.util.ResourceBundle;

@KeyedResources("CincoLobitos.properties")
public class PrintCincoLobitos {

    public static String[] lines() {
        // Constants generated from the properties file keys
        String[] lines = {
                CincoLobitos.SHE_WOLF,
                CincoLobitos.WOLF_CUBS,
                CincoLobitos.SHE_RAISED,
                CincoLobitos.AND_ALL,
                CincoLobitos.SHE_NURSED,
        };
        return lines;
    }

    public static void main(String...args)  {
        Locale[] locales = {
                Locale.ROOT,
                Locale.ENGLISH,
        };

        print(locales, lines(), System.out);
    }

    public static void print(Locale[] locales, String[] lines, PrintStream out) {
        for (int i = 0; i < lines.length; i++) {
            for (Locale locale : locales) {
                ResourceBundle bundle = ResourceBundle.getBundle("uk.autores.consts.CincoLobitos", locale);
                String line = bundle.getString(lines[i]);
                out.println(i + ": " + line);
            }
        }
    }
}
