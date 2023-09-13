package demo;

import uk.autores.ClasspathResource;
import uk.autores.GenerateStringsFromText;

@ClasspathResource(
        value = {
                "Poule.txt",
                "Roses.txt",
        },
        handler = GenerateStringsFromText.class
)
public class PrintRhymes {

    public static void main(String...args)  {
        System.out.println(Poule.text());
        System.out.println(Roses.text());
    }
}
