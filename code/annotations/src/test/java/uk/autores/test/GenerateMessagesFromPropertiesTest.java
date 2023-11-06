package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.GenerateMessagesFromProperties;
import uk.autores.cfg.Format;
import uk.autores.cfg.Localize;
import uk.autores.cfg.MissingKey;
import uk.autores.cfg.Visibility;
import uk.autores.handling.Config;
import uk.autores.handling.ConfigDef;
import uk.autores.handling.Handler;
import uk.autores.test.testing.HandlerResults;
import uk.autores.test.testing.HandlerTester;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateMessagesFromPropertiesTest {

    private final Handler handler = new GenerateMessagesFromProperties();
    private final String filename = "Messages.properties";
    private final String filename_fr = "Messages_fr.properties";

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    private byte[] messages() {
        String data = "today={0} said \"Today is {1,date}!\"\n";
        data += "foo=bar\n";
        return data.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] messages_fr() {
        String data_fr = "today=\"{0} a dit : \u00AB Aujourd'hui, c'est {1,date} ! \u00BB\n";
        data_fr += "foo=baz\n";
        return data_fr.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] messages_fr_differentFormat() {
        String data_fr = "today={0,number}\n";
        data_fr += "foo=baz\n";
        return data_fr.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] messages_fr_empty() {
        return new byte[0];
    }

    private byte[] messagesInvalidIdentifier() {
        return "public=foo".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void checkConfigDefs() {
        Set<ConfigDef> supported = handler.config();
        assertTrue(supported.contains(Visibility.DEF));
        assertTrue(supported.contains(Localize.DEF));
        assertTrue(supported.contains(MissingKey.DEF));
        assertTrue(supported.contains(Format.DEF));
    }

    @Test
    void generatesUnlocalizedMessages() throws Exception {
        HandlerResults hr = tester().withResource(filename, messages())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void generatesLocalizedMessages() throws Exception {
        HandlerResults hr = tester()
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void skipsLocalizedMessages() throws Exception {
        List<Config> cfg = singletonList(new Config(Localize.LOCALIZE, Localize.FALSE));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void skipsFormatMessages() throws Exception {
        List<Config> cfg = singletonList(new Config(Format.FORMAT, Format.FALSE));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void generatesPublicMessages() throws Exception {
        List<Config> cfg = singletonList(new Config(Visibility.VISIBILITY, Visibility.PUBLIC));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
        String src = hr.generatedSource().values().iterator().next();
        assertTrue(src.contains("public final class"));
    }

    @Test
    void reportsMissingLocalizedKeys() throws Exception {
        HandlerResults hr = tester()
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr_empty())
                .test();
        hr.assertErrorMessagesReported();
    }

    @Test
    void allowsMissingLocalizedKeys() throws Exception {
        List<Config> cfg = singletonList(new Config(MissingKey.MISSING_KEY, MissingKey.WARN));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr_empty())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void reportsMismatchedFormatVariables() throws Exception {
        HandlerResults hr = tester()
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr_differentFormat())
                .test();
        hr.assertErrorMessagesReported();
    }

    @Test
    void reportsBadFilename() throws Exception {
        HandlerResults hr = tester()
                .withResource("wrong.dat", messages())
                .test();
        hr.assertErrorMessagesReported();
    }

    @Test
    void reportsInvalidIdentifier() throws Exception {
        HandlerResults hr = tester()
                .withResource(filename, messagesInvalidIdentifier())
                .test();
        hr.assertErrorMessagesReported();
    }
}