package uk.autores.strings;

import uk.autores.ClasspathResource;
import uk.autores.GenerateStringsFromText;

import static uk.autores.ConfigDefs.Names.STRATEGY;
import static uk.autores.ConfigDefs.Names.VISIBILITY;

@ClasspathResource(
        value = {
                "Poule.txt",
                "Roses.txt",
        },
        handler = GenerateStringsFromText.class,
        config = {
                @ClasspathResource.Cfg(key = STRATEGY, value = "inline"),
                @ClasspathResource.Cfg(key = VISIBILITY, value = "public"),
        }
)
public class PrintRhymes {

    public static void main(String...args)  {
        System.out.println(Poule.text());
        System.out.println(Roses.text());
    }
}
