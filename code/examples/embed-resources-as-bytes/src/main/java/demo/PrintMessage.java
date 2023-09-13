package demo;

import org.xml.sax.InputSource;
import uk.autores.ClasspathResource;
import uk.autores.GenerateByteArraysFromFiles;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

@ClasspathResource(
        value = "Message.xml",
        handler = GenerateByteArraysFromFiles.class
)
public class PrintMessage {

    public static void main(String...args) throws XPathExpressionException {
        byte[] raw = Message.bytes();
        String msg = parse(raw);
        System.out.println(msg);
    }

    private static String parse(byte[] raw) throws XPathExpressionException {
        InputSource src = new InputSource();
        src.setByteStream(new ByteArrayInputStream(raw));
        return XPathFactory.newInstance()
                .newXPath()
                .compile("/message")
                .evaluate(src);
    }
}
