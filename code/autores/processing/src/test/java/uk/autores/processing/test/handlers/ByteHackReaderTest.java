// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.handlers;

import org.junit.jupiter.api.Test;
import uk.autores.processing.test.testing.Proxies;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ByteHackReaderTest {

    @Test
    void canReadSimple() throws IOException {
        char[] actual = new char[1];
        byte[] barr = { 0, 'A', };
        BHR ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            BHR bhr = instance(in)) {
            int r = bhr.read(actual);
            assertEquals(1, r);
            assertTrue(bhr.read() < 0);
            ref = bhr;
        }
        assertEquals('A', actual[0]);
        assertFalse(ref.lastByteOdd());
    }

    @Test
    void encodesBytes() throws IOException {
        char[] actual = new char[2 * 0xFFFF];
        byte[] barr = { (byte) 0xFF, 0xA, 0xA, 0 };
        BHR ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            BHR bhr = instance(in)) {
            int r = bhr.read(actual);
            assertEquals(2, r);
            assertTrue(bhr.read() < 0);
            ref = bhr;
        }
        assertEquals('\uFF0A', actual[0]);
        assertEquals('\u0A00', actual[1]);
        assertFalse(ref.lastByteOdd());
    }

    @Test
    void handlesOffsets() throws IOException {
        int off = 10;
        char[] actual = new char[100];
        byte[] barr = { 0, 'A', };
        BHR ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            BHR bhr = instance(in)) {
            int r = bhr.read(actual, off, 10);
            assertEquals(1, r);
            assertTrue(bhr.read() < 0);
            ref = bhr;
        }
        assertEquals('A', actual[off]);
        assertFalse(ref.lastByteOdd());
    }

    @Test
    void handlesSingleByte() throws IOException {
        char[] actual = new char[1];
        byte[] barr = { 19 };
        BHR ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            BHR bhr = instance(in)) {
            int r = bhr.read(actual);
            assertTrue(r < 0);
            ref = bhr;
        }
        assertEquals('\u0000', actual[0]);
        assertTrue(ref.lastByteOdd());
        assertEquals((byte) 19, ref.getOddByte());
    }

    @Test
    void handlesOddNumberBytes() throws IOException {
        char[] actual = new char[1];
        byte[] barr = { 0, 'A', 19 };
        BHR ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            BHR bhr = instance(in)) {
            int r = bhr.read(actual);
            assertEquals(1, r);
            assertTrue(bhr.read() < 0);
            ref = bhr;
        }
        assertEquals('A', actual[0]);
        assertTrue(ref.lastByteOdd());
        assertEquals((byte) 19, ref.getOddByte());
    }

    private BHR instance(InputStream in) {
        return Proxies.instance(BHR.class, "uk.autores.processing.handlers.ByteHackReader")
                .params(InputStream.class).args(in);
    }

    private interface BHR extends Closeable {
        int read(char[] ch, int off, int len) throws IOException;
        int read(char[] ch) throws IOException;
        int read() throws IOException;
        boolean lastByteOdd();
        byte getOddByte();
    }
}
