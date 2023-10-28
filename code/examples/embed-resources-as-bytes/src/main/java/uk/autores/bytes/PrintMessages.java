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
        value = "LazyMessage.xml",
        handler = GenerateByteArraysFromFiles.class,
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.LAZY)
)
@ResourceFiles(
        value = "InlineMessage.xml",
        handler = GenerateByteArraysFromFiles.class,
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.INLINE)
)
@ResourceFiles(
        value = { "EncodedMessage.xml", "EncodedMessage2.xml", },
        handler = GenerateByteArraysFromFiles.class,
        config = @ResourceFiles.Cfg(key = STRATEGY, value = Strategy.ENCODE)
)
public class PrintMessages {

    public static void main(String...args) throws XPathExpressionException {
        parseAndPrint(LazyMessage.bytes());
        parseAndPrint(InlineMessage.bytes());
        parseAndPrint(EncodedMessage.bytes());
        parseAndPrint(EncodedMessage2.bytes());
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
