package demo;

import uk.autores.ClasspathResource;
import uk.autores.GenerateConstantsFromProperties;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

@ClasspathResource(
        value = "Localizations.properties",
        handler = GenerateConstantsFromProperties.class
)
public class PrintLocalizations {

    public static void main(String...args)  {
        printLocalizations(System.out::println);
    }

    static void printLocalizations(Consumer<String> c) {
        for (Locale l : asList(Locale.ENGLISH, Locale.FRENCH, Locale.CANADA_FRENCH, Locale.forLanguageTag("ga"))) {
            ResourceBundle bundle = ResourceBundle.getBundle("demo.Localizations", l);
            String value = bundle.getString(Localizations.LIBERTY);
            c.accept(l.getDisplayName() + ": " + value);
        }
    }
}