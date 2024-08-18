package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;

public final class WbpDecoder {
    private static final float H1 = -0.091271763114f;
    private static final float H2 = -0.057543526229f;
    private static final float H3 = +0.591271763114f;
    private static final float H4 = +1.115087032318f;
    private static final float H5 = +0.591271763114f;
    private static final float H6 = -0.057543526229f;
    private static final float H7 = -0.091271763114f;

    private static final float G0 = +0.026748757411f;
    private static final float G1 = +0.016864118443f;
    private static final float G2 = -0.078223266529f;
    private static final float G3 = -0.266864118443f;
    private static final float G4 = +0.602949018236f;
    private static final float G5 = -0.266864118443f;
    private static final float G6 = -0.078223266529f;
    private static final float G7 = +0.016864118443f;
    private static final float G8 = +0.026748757411f;

    private static final float[] h = {
        0.0f,
        -0.091271763114f,
        -0.057543526229f,
        0.591271763114f,
        1.11508705f,
        0.591271763114f,
        -0.057543526229f,
        -0.091271763114f,
        0.0f
    };
    private static final float[] g = {
        0.026748757411f,
        0.016864118443f,
        -0.078223266529f,
        -0.266864118443f,
        0.602949018236f,
        -0.266864118443f,
        -0.078223266529f,
        0.016864118443f,
        0.026748757411f
    };

    public static void main(String[] args) throws IOException {
        var data = Files.readAllBytes(Path.of("D:\\Jan\\Desktop\\Untitled2"));
        var source = new ByteArrayDataSource(data);

        var surface = Surface.create(
            2048,
            2048,
            TextureFormat.A8UNorm
        );

        int i = 0;
        while (source.tell() < source.size()) {
            var tile = ImageTile.read(source);
            var tileDecompressed = Buffers.toArray(Decompressor
                .forType(CompressionType.Kraken)
                .decompress(ByteBuffer.wrap(tile.data()), tile.size()));

            byte[] decoded = WbpDecoder.decode(tile, tileDecompressed);
            var tileSurface = new Surface(
                tile.width(),
                tile.height(),
                TextureFormat.A8UNorm,
                decoded
            );
            surface.copyFrom(tileSurface, tile.x(), tile.y());
            saveImage(tileSurface.data(), tileSurface.width(), tileSurface.height(), "D:\\Jan\\Desktop\\colossus\\tile%02d.png".formatted(i++));
            System.out.println(tile);

//            break;
        }

        saveImage(surface.data(), surface.width(), surface.height(), "D:\\Jan\\Desktop\\colossus\\test.png");
    }

    private WbpDecoder() {
    }

    public static byte[] decode(ImageTile tile, byte[] data) throws IOException {
        int w0 = tile.width();
        int w1 = (w0 / 2 + 4);
        int w2 = (w1 / 2 + 4);

        int h0 = tile.height();
        int h1 = (h0 / 2 + 4);
        int h2 = (h1 / 2 + 4);

        var h1Offset = h2 * w2 * 4;
        var l0Offset = h1Offset - h1 * w1;

        var level1 = decodeTile(data, 0, w1, h1, tile.coefficients()[1], tile.coefficients()[2], tile.coefficients()[3], tile);
//        var level1 = decodeTile(data, 0, w1, h1, tile.coefficients()[4], tile.coefficients()[5], tile.coefficients()[6], tile);
        System.arraycopy(level1, 0, data, l0Offset, level1.length);
//        var level0 = decodeTile(data, l0Offset, w0, h0, tile.coefficients()[1], tile.coefficients()[2], tile.coefficients()[3], tile);
        var level0 = decodeTile(data, l0Offset, w0, h0, tile.coefficients()[4], tile.coefficients()[5], tile.coefficients()[6], tile);

        saveImage(level0, w0, h0, "D:\\Jan\\Desktop\\colossus\\transform%04d-%04d.png".formatted(tile.x(), tile.y()));

        return level0;
    }

    static void saveImage(byte[] bytes, int width, int height, String filename) throws IOException {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, width, height, bytes);
        ImageIO.write(image, "png", new File(filename));
        // Files.write(Path.of(filename), bytes);
    }

    private static byte[] transpose(byte[] bytes, int width, int height) {
        byte[] temp = new byte[bytes.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x * height + y] = bytes[y * width + x];
            }
        }
        return temp;
    }

    private static byte[] decodeTile(
        byte[] coefficients,
        int offset,
        int width,
        int height,
        float weight1,
        float weight2,
        float weight3,
        ImageTile tile
    ) throws IOException {
        int pageWidthBlocks = width / 4;
        int pageHeightBlocks = height / 4;

        int subBandWidth = pageWidthBlocks * 2;
        int subBandHeight = pageHeightBlocks * 2;
        int subBandPitch = subBandWidth + 4;
        int subBandSize = (subBandHeight + 4) * subBandPitch;

        int baseLLIndex = offset + subBandPitch * 2;
        int baseHLIndex = offset + subBandPitch * 2 + subBandSize * 1;
        int baseLHIndex = offset + subBandPitch * 2 + subBandSize * 2;
        int baseHHIndex = offset + subBandPitch * 2 + subBandSize * 3;

        int outBandPitch = subBandWidth + 4;
        int outBandHeight = subBandHeight * 2;
        int outBandSize = outBandPitch * outBandHeight;
        int baseLIndex = 0;
        int baseHIndex = 0 + outBandSize;

        float[] temp = new float[outBandSize * 2];
        for (int y = 0; y < subBandHeight; y++) {
            for (int x = 0; x < subBandPitch; x++) {
                int subBandOffset = y * subBandPitch + x;
                float ll1 = unpackL(coefficients[baseLLIndex + subBandOffset - subBandPitch * 1]);
                float ll2 = unpackL(coefficients[baseLLIndex + subBandOffset + subBandPitch * 0]);
                float ll3 = unpackL(coefficients[baseLLIndex + subBandOffset + subBandPitch * 1]);
                float ll4 = unpackL(coefficients[baseLLIndex + subBandOffset + subBandPitch * 2]);

                float lh0 = unpackH(coefficients[baseLHIndex + subBandOffset - subBandPitch * 2]) * weight1;
                float lh1 = unpackH(coefficients[baseLHIndex + subBandOffset - subBandPitch * 1]) * weight1;
                float lh2 = unpackH(coefficients[baseLHIndex + subBandOffset + subBandPitch * 0]) * weight1;
                float lh3 = unpackH(coefficients[baseLHIndex + subBandOffset + subBandPitch * 1]) * weight1;
                float lh4 = unpackH(coefficients[baseLHIndex + subBandOffset + subBandPitch * 2]) * weight1;

                temp[baseLIndex + (y * 2 + 0) * outBandPitch + x] = f1(ll1, ll2, ll3, lh0, lh1, lh2, lh3);
                temp[baseLIndex + (y * 2 + 1) * outBandPitch + x] = f2(ll1, ll2, ll3, ll4, lh0, lh1, lh2, lh3, lh4);

                float hl1 = unpackH(coefficients[baseHLIndex + subBandOffset - subBandPitch * 1]) * weight2;
                float hl2 = unpackH(coefficients[baseHLIndex + subBandOffset + subBandPitch * 0]) * weight2;
                float hl3 = unpackH(coefficients[baseHLIndex + subBandOffset + subBandPitch * 1]) * weight2;
                float hl4 = unpackH(coefficients[baseHLIndex + subBandOffset + subBandPitch * 2]) * weight2;

                float hh0 = unpackH(coefficients[baseHHIndex + subBandOffset - subBandPitch * 2]) * weight3;
                float hh1 = unpackH(coefficients[baseHHIndex + subBandOffset - subBandPitch * 1]) * weight3;
                float hh2 = unpackH(coefficients[baseHHIndex + subBandOffset + subBandPitch * 0]) * weight3;
                float hh3 = unpackH(coefficients[baseHHIndex + subBandOffset + subBandPitch * 1]) * weight3;
                float hh4 = unpackH(coefficients[baseHHIndex + subBandOffset + subBandPitch * 2]) * weight3;

                temp[baseHIndex + (y * 2 + 0) * outBandPitch + x] = f1(hl1, hl2, hl3, hh0, hh1, hh2, hh3);
                temp[baseHIndex + (y * 2 + 1) * outBandPitch + x] = f2(hl1, hl2, hl3, hl4, hh0, hh1, hh2, hh3, hh4);
            }
        }

        byte[] tempBytes = new byte[temp.length];
        for (int i = 0; i < temp.length; i++) {
            tempBytes[i] = MathF.packUNorm8(temp[i]);
        }
        saveImage(tempBytes, outBandPitch, outBandHeight * 2, "D:\\Jan\\Desktop\\colossus\\temp-%d-%04d-%04d.png".formatted(offset == 0 ? 0 : 1, tile.x(), tile.y()));

//        System.exit(1);

        byte[] result = new byte[width * height];
        for (int y = 0; y < outBandHeight; y++) {
            for (int x = 0; x < subBandWidth; x++) {
                int bandRowOffset = y * outBandPitch + 2 + x;

                // float l0 = ...
                float l1 = temp[baseLIndex + bandRowOffset - 1];
                float l2 = temp[baseLIndex + bandRowOffset + 0];
                float l3 = temp[baseLIndex + bandRowOffset + 1];
                float l4 = temp[baseLIndex + bandRowOffset + 2];

                float h0 = temp[baseHIndex + bandRowOffset - 2];
                float h1 = temp[baseHIndex + bandRowOffset - 1];
                float h2 = temp[baseHIndex + bandRowOffset + 0];
                float h3 = temp[baseHIndex + bandRowOffset + 1];
                float h4 = temp[baseHIndex + bandRowOffset + 2];

                float v0 = f1(l1, l2, l3, h0, h1, h2, h3);
                float v1 = f2(l1, l2, l3, l4, h0, h1, h2, h3, h4);

                result[y * width + x * 2 + 0] = MathF.packUNorm8(v0);
                result[y * width + x * 2 + 1] = MathF.packUNorm8(v1);
            }
        }

        saveImage(result, width, height, "D:\\Jan\\Desktop\\colossus\\temp-2-%04d-%04d.png".formatted(tile.x(), tile.y()));

        return result;
//        return null;
    }

    private static float f1(float l1, float l2, float l3, float h0, float h1, float h2, float h3) {
        return l1 * H2 + l2 * H4 + l3 * H6 + h0 * G1 + h1 * G3 + h2 * G5 + h3 * G7;
    }

    private static float f2(float l1, float l2, float l3, float l4, float h0, float h1, float h2, float h3, float h4) {
        return l1 * H1 + l2 * H3 + l3 * H5 + l4 * H7 + h0 * G0 + h1 * G2 + h2 * G4 + h3 * G6 + h4 * G8;
    }

    private static float unpackL(byte b) {
        return Byte.toUnsignedInt(b) * (1.0f / 255.0f);
    }

    private static float unpackH(byte b) {
        return Byte.toUnsignedInt(b) * (2.0f / 254.0f) - 1.0f;
    }

    private static float[] transpose(float[] floats, int width, int height) {
        float[] result = new float[floats.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[x * width + y] = floats[y * width + x];
            }
        }
        return result;
    }
}
