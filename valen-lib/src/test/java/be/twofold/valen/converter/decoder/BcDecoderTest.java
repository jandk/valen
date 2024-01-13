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

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBc3() throws IOException {
        byte[] src = readResource("/bc3.dds", 128);

        byte[] actual = new BC3Decoder().decode(src, 256, 256);
        byte[] expected = readPng("/bc3.png");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBc4u() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/ati1.dds", 128), 2048);

        byte[] actual = new BC4UDecoder().decode(src, 64, 64);
        byte[] expected = readPng("/ati1.png");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testBc5u() throws IOException {
        byte[] src = Arrays.copyOf(readResource("/ati2.dds", 128), 65536);

        byte[] actual = new BC5UDecoder(false).decode(src, 256, 256);
        byte[] expected = readPng("/ati2.png");

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
