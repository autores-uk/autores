package uk.autores.bytes;

import org.xml.sax.InputSource;
import uk.autores.GenerateByteArraysFromFiles;
import uk.autores.ResourceFiles;
import uk.autores.cfg.Strategy;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

import static uk.autores.cfg.Strategy.STRATEGY;

@ResourceFiles(
        value = "Utf16LazyMessage.xml",
        handler = GenerateByteArraysFromFiles.class,
        // loaded at runtime using Class.getResourceAsStream(String)
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.LAZY)
)
@ResourceFiles(
        value = "Utf16InlineMessage.xml",
        handler = GenerateByteArraysFromFiles.class,
        // stored in class file as byte code instructions
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.INLINE)
)
@ResourceFiles(
        value = { "Utf16EncodedMessage.xml", "Utf8EncodedMessage.xml", },
        handler = GenerateByteArraysFromFiles.class,
        // stored in class file as string constant
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.ENCODE)
)
public class PrintMessages {

    public static void main(String...args) throws XPathExpressionException {
        parseAndPrint(Utf16LazyMessage.bytes());
        parseAndPrint(Utf16InlineMessage.bytes());
        parseAndPrint(Utf16EncodedMessage.bytes());
        parseAndPrint(Utf8EncodedMessage.bytes());
    }

    private static void parseAndPrint(byte[] raw) throws XPathExpressionException {
        InputSource src = new InputSource();
        src.setByteStream(new ByteArrayInputStream(raw));
        String msg = XPathFactory.newInstance()
                .newXPath()
                .compile("/message")
                .evaluate(src);
        System.out.println(msg);
    }
}
