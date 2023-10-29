package uk.autores.test.internal;

import org.junit.jupiter.api.Test;
import uk.autores.internal.ByteHackReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ByteHackReaderTest {

    @Test
    void canReadSimple() throws IOException {
        char[] actual = new char[1];
        byte[] barr = { 0, 'A', };
        ByteHackReader ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            ByteHackReader bhr = new ByteHackReader(in)) {
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
        ByteHackReader ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            ByteHackReader bhr = new ByteHackReader(in)) {
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
        ByteHackReader ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            ByteHackReader bhr = new ByteHackReader(in)) {
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
        ByteHackReader ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            ByteHackReader bhr = new ByteHackReader(in)) {
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
        ByteHackReader ref;
        try(InputStream in = new ByteArrayInputStream(barr);
            ByteHackReader bhr = new ByteHackReader(in)) {
            int r = bhr.read(actual);
            assertEquals(1, r);
            assertTrue(bhr.read() < 0);
            ref = bhr;
        }
        assertEquals('A', actual[0]);
        assertTrue(ref.lastByteOdd());
        assertEquals((byte) 19, ref.getOddByte());
    }
}
