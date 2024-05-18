package be.twofold.valen.converter.decoder;

import org.junit.jupiter.api.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BcDecoderTest {

    @Test
    void testBc1NoAlpha() throws IOException {
        byte[] src = readResource("/bc1.dds", 148);

        byte[] actual = new BC1Decoder().decode(src, 256, 256);
        byte[] expected = readPng("/bc1.png");

        compareBC(actual, expected);
    }

    @Test
    void testBc2() throws IOException {
        byte[] src = readResource("/bc2.dds", 128);

        byte[] actual = new BC2Decoder().decode(src, 256, 256);
        byte[] expected = readPng("/bc2.png");

        compareBC(actual, expected);
    }

    @Test
    void testBc3() throws IOException {
        byte[] src = readResource("/bc3.dds", 128);

        byte[] actual = new BC3Decoder().decode(src, 256, 256);
        byte[] expected = readPng("/bc3.png");

        compareBC(actual, expected);
    }

    @Test
    void testBc4u() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/bc4.dds", 128), 2048);

        byte[] actual = new BC4UDecoder().decode(src, 64, 64);
        byte[] expected = readPng("/bc4.png");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBc5u() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/bc5.dds", 128), 65536);

        byte[] actual = new BC5UDecoder(false).decode(src, 256, 256);
        byte[] expected = readPng("/bc5.png");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBC6H_UF16() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/bc6h_uf16.dds", 148), 16384);

        BC6Decoder decoder = new BC6Decoder(false);
        byte[] actual = decoder.decode(src, 128, 128);
        byte[] expected = readDDSFP16("/bc6h_uf16_out.dds");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBC6H_SF16() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/bc6h_sf16.dds", 148), 16384);

        BC6Decoder decoder = new BC6Decoder(true);
        byte[] actual = decoder.decode(src, 128, 128);
        byte[] expected = readDDSFP16("/bc6h_sf16_out.dds");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBc7() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/bc7.dds", 148), 65536);

        byte[] actual = new BC7Decoder().decode(src, 256, 256);
        byte[] expected = readPng("/bc7.png");

        assertThat(actual).isEqualTo(expected);
    }

    private static byte[] readResource(String path, int offset) throws IOException {
        try (var in = BcDecoderTest.class.getResourceAsStream(path)) {
            in.skipNBytes(offset);
            return in.readAllBytes();
        }
    }

    private static byte[] readPng(String path) throws IOException {
        try (var in = BcDecoderTest.class.getResourceAsStream(path)) {
            BufferedImage image = ImageIO.read(in);
            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            if (image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
                swizzle4(data);
            }
            if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                swizzle3(data);
            }
            return data;
        }
    }

    private static byte[] readDDSFP16(String path) throws IOException {
        byte[] read = Arrays.copyOf(readResource(path, 148), 128 * 128 * 8);

        byte[] expected = new byte[read.length * 3 / 4];
        for (int i = 0, j = 0; i < read.length; i += 8, j += 6) {
            System.arraycopy(read, i, expected, j, 6);
        }
        return expected;
    }

    private static void compareBC(byte[] actual, byte[] expected) {
        assertThat(actual).hasSameSizeAs(expected);

        for (int i = 0; i < expected.length; i += 4) {
            int r1 = Byte.toUnsignedInt(actual[i + 0]);
            int g1 = Byte.toUnsignedInt(actual[i + 1]);
            int b1 = Byte.toUnsignedInt(actual[i + 2]);
            int a1 = Byte.toUnsignedInt(actual[i + 3]);

            int r0 = Byte.toUnsignedInt(expected[i + 0]);
            int g0 = Byte.toUnsignedInt(expected[i + 1]);
            int b0 = Byte.toUnsignedInt(expected[i + 2]);
            int a0 = Byte.toUnsignedInt(expected[i + 3]);

            if (Math.abs(r0 - r1) > 1 || Math.abs(g0 - g1) > 1 || Math.abs(b0 - b1) > 1 || a0 != a1) {
                fail("i: " + i + " r0: " + r0 + " g0: " + g0 + " b0: " + b0 + " a0: " + a0 + " r1: " + r1 + " g1: " + g1 + " b1: " + b1 + " a1: " + a1);
            }
        }
    }

    private static void swizzle3(byte[] data) {
        for (int i = 0; i < data.length; i += 3) {
            swap(data, i + 0, i + 2);
        }
    }

    private static void swizzle4(byte[] array) {
        for (int i = 0; i < array.length; i += 4) {
            swap(array, i + 0, i + 3);
            swap(array, i + 1, i + 2);
        }
    }

    private static void swap(byte[] array, int i, int j) {
        byte tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
