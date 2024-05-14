// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.handling;

import org.junit.jupiter.api.Test;
import uk.autores.handling.*;
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
    private final String filename_fr_CA = "Messages_fr_CA.properties";

    private HandlerTester tester() {
        return new HandlerTester(handler);
    }

    private byte[] messages() {
        String data = "today={0} said \"Today is {1,date}!\"\n";
        data += "foo=bar\n";
        data += "planet-event=At {1,time,short} on {1,time,EEEE} {2,date}, there was {3} on planet {0,number,integer}.\n";
        data = addCases(data);
        return data.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] messages_fr() {
        String data_fr = "today=\"{0} a dit : \u00AB Aujourd'hui, c'est {1,date} ! \u00BB\n";
        data_fr += "foo=baz\n";
        data_fr += "planet-event=At {1,time} on {2,date}, there was {3} on planet {0,number,integer}.\n";
        data_fr = addCases(data_fr);
        return data_fr.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] messages_fr_differentFormat() {
        String data_fr = "today={0,number}\n";
        data_fr += "foo=baz\n";
        data_fr += "planet-event=At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.\n";
        data_fr = addCases(data_fr);
        return data_fr.getBytes(StandardCharsets.UTF_8);
    }

    private String addCases(String s) {
        s += "missing-args={10}\n";
        s += "number={0,number}\n";
        s += "custom-number={0,number,#,##0.00;(#,##0.00)}\n";
        s += "percent={0,number,percentage}\n";
        s += "currency={0,number,currency}\n";
        s += "date-short={0,date,short}\n";
        s += "date-medium={0,date,medium}\n";
        s += "date-long={0,date,long}\n";
        s += "date-full={0,date,full}\n";
        s += "date={0,date,EEEE}\n";
        s += "time-short={0,time,short}\n";
        s += "time-medium={0,time,medium}\n";
        s += "time-long={0,time,long}\n";
        s += "time-full={0,time,full}\n";
        s += "time={0,time,EEEE}\n";
        s += "choice=\"There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.\"";
        return s;
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
        assertTrue(supported.contains(CfgVisibility.DEF));
        assertTrue(supported.contains(CfgLocalize.DEF));
        assertTrue(supported.contains(CfgMissingKey.DEF));
        assertTrue(supported.contains(CfgFormat.DEF));
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
        List<Config> cfg = singletonList(new Config(CfgLocalize.LOCALIZE, CfgLocalize.FALSE));
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
        List<Config> cfg = singletonList(new Config(CfgFormat.FORMAT, CfgFormat.FALSE));
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
        List<Config> cfg = singletonList(new Config(CfgVisibility.VISIBILITY, CfgVisibility.PUBLIC));
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
        List<Config> cfg = singletonList(new Config(CfgMissingKey.MISSING_KEY, CfgMissingKey.WARN));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr_empty())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);
    }

    @Test
    void substitutesMissingLocalizedKeys() throws Exception {
        List<Config> cfg = singletonList(new Config(CfgMissingKey.MISSING_KEY, CfgMissingKey.WARN));
        HandlerResults hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr())
                .withUnspecifiedFile(filename_fr_CA, messages_fr_empty())
                .test();
        hr.assertNoErrorMessagesReported();
        hr.assertAllGeneratedFilesCompile(1);

        hr = tester()
                .withConfig(cfg)
                .withResource(filename, messages())
                .withUnspecifiedFile(filename_fr, messages_fr_empty())
                .withUnspecifiedFile(filename_fr_CA, messages_fr_empty())
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