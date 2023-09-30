package demo;

import uk.autores.ClasspathResource;
import uk.autores.GenerateMessagesFromProperties;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

import static uk.autores.ConfigDefs.Names.LOCALIZE;
import static uk.autores.ConfigDefs.Names.MISSING_KEY;

@ClasspathResource(
        value = "Non-nls.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ClasspathResource.Cfg(key = LOCALIZE, value = "false")
)
@ClasspathResource(
        value = "Messages.properties",
        handler = GenerateMessagesFromProperties.class
)
@ClasspathResource(
        value = "Sparse.properties",
        handler = GenerateMessagesFromProperties.class,
        config = @ClasspathResource.Cfg(key = MISSING_KEY, value = "ignore")
)
public class PrintProperties {

    public static void main(String...args)  {
        Consumer<String> stdout = System.out::println;
        printAppName(stdout);
        printHelloWorld(stdout);
        printPlanetEvent(stdout);
    }

    static void printAppName(Consumer<String> c) {
        c.accept(Non_nls.application_name());
    }

    static void printHelloWorld(Consumer<String> c) {
        c.accept("base: " + Messages.hello_world(Locale.ENGLISH));
        c.accept("fr: " + Messages.hello_world(Locale.FRENCH));
        c.accept("fr_CA: " + Messages.hello_world(Locale.CANADA_FRENCH));
    }

    static void printPlanetEvent(Consumer<String> c) {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        c.accept(Non_nls.planet_event(Locale.ENGLISH, gmt, 4, Instant.EPOCH, "an attack"));
    }
}
