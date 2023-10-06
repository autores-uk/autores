package uk.autores.consts;

import uk.autores.ClasspathResource;
import uk.autores.GenerateConstantsFromProperties;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static uk.autores.ConfigDefs.Names.VISIBILITY;

@ClasspathResource(
        value = "Localizations.properties",
        handler = GenerateConstantsFromProperties.class
)
public class PrintLocalizations {

    public static void main(String...args)  {
        printLocalizations(System.out::println);
    }

    public static void printLocalizations(Consumer<String> c) {
        for (Locale l : asList(Locale.ENGLISH, Locale.FRENCH, Locale.CANADA_FRENCH, Locale.forLanguageTag("ga"))) {
            ResourceBundle bundle = ResourceBundle.getBundle("uk.autores.consts.Localizations", l);
            String value = bundle.getString(Localizations.LIBERTY);
            c.accept(l.getDisplayName() + ": " + value);
        }
    }
}
