// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test;

import org.junit.jupiter.api.Test;
import uk.autores.test.testing.Proxies;

import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ModifiedUtf8BufferTest {

    /** Bridge at night emoji */
    private static final String SURROGATE_PAIR = new String(Character.toChars(0x1F309));
    private static final String EURO = "\u20AC";
    private static final String MATH_FRACTURE_G = new String(Character.toChars(0x1D50A));
    private static final String POUND = "\u00A3";

    @Test
    void doesNotExceedUtf8Limit() throws IOException {
        String src = IntStream.range(0, 1024)
                .mapToObj(i -> " abcdef" + EURO + SURROGATE_PAIR + MATH_FRACTURE_G + POUND)
                .collect(Collectors.joining());
        Reader reader = new StringReader(src);

        int maxUtf8Len = 3;
        MUB buf = instance(maxUtf8Len);

        while(buf.receive(reader)) {
            int utfLen = modifiedUtfLen(buf.toString());
            assertTrue(utfLen <= 3, utfLen + "<=" + 3);
        }
    }

    @Test
    void canCreateReadData() throws IOException {
        MUB buf = instance(1024);

        boolean received = buf.receive(new StringReader("123"));
        assertTrue(received);

        assertEquals(3, buf.length());
        assertEquals('1', buf.charAt(0));
        assertEquals('3', buf.charAt(2));

        received = buf.receive(new StringReader(""));
        assertFalse(received);
        assertEquals(0, buf.length());
    }

    @Test
    void canCreateSubsequence() throws IOException {
        MUB buf = instance(1024);

        assertEquals("", buf.toString());
        assertEquals("", buf.subSequence(0, 0));

        buf.receive(new StringReader("123"));

        assertEquals("123", buf.toString());
        assertEquals("2", buf.subSequence(1, 2));
    }

    @Test
    void checksBounds() throws IOException {
        MUB buf = instance(1024);
        buf.receive(new StringReader("123"));

        assertThrowsExactly(IndexOutOfBoundsException.class, () -> buf.charAt(100));
    }

    @Test
    void byteLen() {
        MUB ModifiedUtf8Buffer = instance(10);
        // https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7
        assertEquals(2, ModifiedUtf8Buffer.byteLen('\u0000'));
        assertEquals(1, ModifiedUtf8Buffer.byteLen('\u0001'));
        assertEquals(1, ModifiedUtf8Buffer.byteLen('\u007F'));
        assertEquals(2, ModifiedUtf8Buffer.byteLen('\u0080'));
        assertEquals(2, ModifiedUtf8Buffer.byteLen('\u07FF'));
        assertEquals(3, ModifiedUtf8Buffer.byteLen('\u8000'));
        assertEquals(3, ModifiedUtf8Buffer.byteLen('\uFFFF'));
    }

    private int modifiedUtfLen(String s) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        // DataOutput.writeUTF writes modified UTF-8
        try (DataOutputStream dos = new DataOutputStream(buf)) {
            // writes length first as two byte unsigned short
            dos.writeUTF(s);
        }
        return buf.size() - 2;
    }

    private MUB instance(int n) {
        Class<?>[] types = {Integer.TYPE};
        Object[] args = {n};
        return Proxies.instance(MUB.class, "uk.autores.ModifiedUtf8Buffer", types, args);
    }

    private interface MUB {
        int byteLen(char c);
        char charAt(int idx);
        int length();
        CharSequence subSequence(int i, int j);
        boolean receive(Reader r) throws IOException;
        MUB allocate(int n);
    }
}
