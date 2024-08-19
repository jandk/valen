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

    public static void main(String[] args) throws IOException {
        var data = Files.readAllBytes(Path.of("D:\\Jan\\Desktop\\Untitled1"));
        var source = new ByteArrayDataSource(data);

        var surface = Surface.create(
            2048,
            2048,
            TextureFormat.R8G8UNorm
        );

        while (source.tell() < source.size()) {
            var tile = ImageTile.read(source);
            var tileDecompressed = Buffers.toArray(Decompressor
                .forType(CompressionType.Kraken)
                .decompress(ByteBuffer.wrap(tile.data()), tile.size()));

            byte[] decoded = WbpDecoder.decode(tile, tileDecompressed);
            var tileSurface = new Surface(
                tile.width(),
                tile.height(),
                TextureFormat.R8G8UNorm,
                decoded
            );
            surface.copyFrom(tileSurface, tile.x(), tile.y());
            System.out.println(tile);
        }
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

        var subBandLSize = h2 * w2 * 4;
        var subBandHSize = h1 * w1;
        var subBandSize = subBandLSize + 3 * subBandHSize;
        var subBandShift = subBandLSize - subBandHSize;

        int numBands = switch (tile.format()) {
            case 24 -> 1;
            case 25 -> 2;
            default -> throw new IllegalArgumentException("Unsupported tile format: " + tile.format());
        };

        byte[] temp = new byte[subBandHSize];
        byte[] output = new byte[w0 * h0 * numBands];
        for (int subBand = 0; subBand < numBands; subBand++) {
            int subBandOffset = subBand * subBandSize;
            decodeTile(data, subBandOffset, temp, 1, 0, w1, h1, tile, 1);
            System.arraycopy(temp, 0, data, subBandOffset + subBandShift, temp.length);
            decodeTile(data, subBandOffset + subBandShift, output, numBands, subBand, w0, h0, tile, 4);
        }

        return output;
    }

    private static void saveImage3(byte[] bytes, int width, int height, String filename) throws IOException {
        byte[] rgb = new byte[width * height * 3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int offset = y * width + x;
                rgb[offset * 3 + 0] = bytes[offset * 2 + 0];
                rgb[offset * 3 + 1] = bytes[offset * 2 + 1];
                rgb[offset * 3 + 2] = 0;
            }
        }
        var image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, rgb);
        ImageIO.write(image, "png", new File(filename));
    }

    private static void decodeTile(
        byte[] src,
        int srcOffset,
        byte[] dst,
        int dstPitch,
        int dstOffset,
        int width,
        int height,
        ImageTile tile,
        int scaleOffset
    ) {
        int pageWidthBlocks = width / 4;
        int pageHeightBlocks = height / 4;

        int subBandWidth = pageWidthBlocks * 2;
        int subBandHeight = pageHeightBlocks * 2;
        int subBandPitch = subBandWidth + 4;
        int subBandSize = (subBandHeight + 4) * subBandPitch;

        int baseLLIndex = srcOffset + subBandPitch * 2;
        int baseHLIndex = srcOffset + subBandPitch * 2 + subBandSize * 1;
        int baseLHIndex = srcOffset + subBandPitch * 2 + subBandSize * 2;
        int baseHHIndex = srcOffset + subBandPitch * 2 + subBandSize * 3;

        int outBandPitch = subBandWidth + 4;
        int outBandHeight = subBandHeight * 2;
        int outBandSize = outBandPitch * outBandHeight;
        int baseLIndex = 0;
        int baseHIndex = 0 + outBandSize;

        float scale1 = tile.scales()[scaleOffset + 0];
        float scale2 = tile.scales()[scaleOffset + 1];
        float scale3 = tile.scales()[scaleOffset + 2];

        float[] temp = new float[outBandSize * 2];
        for (int y = 0; y < subBandHeight; y++) {
            for (int x = 0; x < subBandPitch; x++) {
                int subBandOffset = y * subBandPitch + x;
                float ll1 = unpackLow(src[baseLLIndex + subBandOffset - subBandPitch * 1]);
                float ll2 = unpackLow(src[baseLLIndex + subBandOffset + subBandPitch * 0]);
                float ll3 = unpackLow(src[baseLLIndex + subBandOffset + subBandPitch * 1]);
                float ll4 = unpackLow(src[baseLLIndex + subBandOffset + subBandPitch * 2]);

                float lh0 = unpackHigh(src[baseLHIndex + subBandOffset - subBandPitch * 2]) * scale1;
                float lh1 = unpackHigh(src[baseLHIndex + subBandOffset - subBandPitch * 1]) * scale1;
                float lh2 = unpackHigh(src[baseLHIndex + subBandOffset + subBandPitch * 0]) * scale1;
                float lh3 = unpackHigh(src[baseLHIndex + subBandOffset + subBandPitch * 1]) * scale1;
                float lh4 = unpackHigh(src[baseLHIndex + subBandOffset + subBandPitch * 2]) * scale1;

                temp[baseLIndex + (y * 2 + 0) * outBandPitch + x] = f1(ll1, ll2, ll3, lh0, lh1, lh2, lh3);
                temp[baseLIndex + (y * 2 + 1) * outBandPitch + x] = f2(ll1, ll2, ll3, ll4, lh0, lh1, lh2, lh3, lh4);

                float hl1 = unpackHigh(src[baseHLIndex + subBandOffset - subBandPitch * 1]) * scale2;
                float hl2 = unpackHigh(src[baseHLIndex + subBandOffset + subBandPitch * 0]) * scale2;
                float hl3 = unpackHigh(src[baseHLIndex + subBandOffset + subBandPitch * 1]) * scale2;
                float hl4 = unpackHigh(src[baseHLIndex + subBandOffset + subBandPitch * 2]) * scale2;

                float hh0 = unpackHigh(src[baseHHIndex + subBandOffset - subBandPitch * 2]) * scale3;
                float hh1 = unpackHigh(src[baseHHIndex + subBandOffset - subBandPitch * 1]) * scale3;
                float hh2 = unpackHigh(src[baseHHIndex + subBandOffset + subBandPitch * 0]) * scale3;
                float hh3 = unpackHigh(src[baseHHIndex + subBandOffset + subBandPitch * 1]) * scale3;
                float hh4 = unpackHigh(src[baseHHIndex + subBandOffset + subBandPitch * 2]) * scale3;

                temp[baseHIndex + (y * 2 + 0) * outBandPitch + x] = f1(hl1, hl2, hl3, hh0, hh1, hh2, hh3);
                temp[baseHIndex + (y * 2 + 1) * outBandPitch + x] = f2(hl1, hl2, hl3, hl4, hh0, hh1, hh2, hh3, hh4);
            }
        }

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

                dst[(y * width + x * 2) * dstPitch + dstOffset] = MathF.packUNorm8(v0);
                dst[(y * width + x * 2) * dstPitch + dstPitch + dstOffset] = MathF.packUNorm8(v1);
            }
        }
    }

    private static float f1(float l1, float l2, float l3, float h0, float h1, float h2, float h3) {
        return l1 * H2 + l2 * H4 + l3 * H6 + h0 * G1 + h1 * G3 + h2 * G5 + h3 * G7;
    }

    private static float f2(float l1, float l2, float l3, float l4, float h0, float h1, float h2, float h3, float h4) {
        return l1 * H1 + l2 * H3 + l3 * H5 + l4 * H7 + h0 * G0 + h1 * G2 + h2 * G4 + h3 * G6 + h4 * G8;
    }

    private static float unpackLow(byte b) {
        return Byte.toUnsignedInt(b) * (1.0f / 255.0f);
    }

    private static float unpackHigh(byte b) {
        return Byte.toUnsignedInt(b) * (2.0f / 254.0f) - 1.0f;
    }

}
