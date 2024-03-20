// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.bytes;

import org.xml.sax.InputSource;
import uk.autores.ByteArrayResources;
import uk.autores.Strategy;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

@ByteArrayResources(
        value = "Utf16LazyMessage.xml",
        // loaded at runtime using Class.getResourceAsStream(String)
        strategy = Strategy.LAZY
)
@ByteArrayResources(
        value = "Utf16InlineMessage.xml",
        // stored in class file as byte code instructions
        strategy = Strategy.INLINE
)
@ByteArrayResources(
        value = { "Utf16EncodedMessage.xml", "Utf8EncodedMessage.xml", },
        // stored in class file as string constant
        strategy = Strategy.ENCODE
)
public class PrintMessages {

    public static void main(String...args) throws XPathExpressionException {
        // These are the generated classes; the XML parser will infer the text encoding of the files
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
