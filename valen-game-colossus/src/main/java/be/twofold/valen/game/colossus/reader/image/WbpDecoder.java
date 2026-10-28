package be.twofold.valen.game.colossus.reader.image;

import wtf.reversed.toolbox.math.*;

public final class WbpDecoder {
    private static final float H1 = -0.091271763114f;
    private static final float H2 = -0.057543526229f;
    private static final float H3 = +0.591271763114f;
    private static final float H4 = +1.115087032318f;

    private static final float G0 = +0.026748757411f;
    private static final float G1 = +0.016864118443f;
    private static final float G2 = -0.078223266529f;
    private static final float G3 = -0.266864118443f;
    private static final float G4 = +0.602949018236f;

    private static final float[] LO;
    private static final float[] HI;

    static {
        LO = new float[256];
        HI = new float[256];
        for (int i = 0; i < 256; i++) {
            LO[i] = FloatMath.unpackUNorm8((byte) i);
            HI[i] = Byte.toUnsignedInt((byte) i) * (2.0f / 254.0f) - 1.0f;
        }
    }

    private WbpDecoder() {
    }

    public static byte[] decode(ImageTile tile, byte[] data) {
        int width = tile.width();
        int llWidth = padded(width);

        int height = tile.height();
        int llHeight = padded(height);

        var level1BandSize = subBandSize(width, height);
        var level2Size = 4 * subBandSize(llWidth, llHeight);
        var channelSize = level2Size + 3 * level1BandSize;
        var level1Offset = level2Size - level1BandSize;

        int numChannels = switch (tile.format()) {
            case 24 -> 1;
            case 25 -> 2;
            default -> throw new IllegalArgumentException("Unsupported tile format: " + tile.format());
        };

        byte[] ll1 = new byte[level1BandSize];
        byte[] output = new byte[width * height * numChannels];
        for (int channel = 0; channel < numChannels; channel++) {
            int base = channel * channelSize;
            decodeLevel(data, base, ll1, 1, 0, llWidth, llHeight, tile, 1);
            System.arraycopy(ll1, 0, data, base + level1Offset, ll1.length);
            decodeLevel(data, base + level1Offset, output, numChannels, channel, width, height, tile, 4);
        }

        return output;
    }

    private static void decodeLevel(
        byte[] src,
        int srcOffset,
        byte[] dst,
        int stride,
        int channel,
        int width,
        int height,
        ImageTile tile,
        int scaleOffset
    ) {
        int pitch = padded(width);
        int bandSize = subBandSize(width, height);

        int ll = srcOffset;
        int hl = srcOffset + bandSize;
        int lh = srcOffset + bandSize * 2;
        int hh = srcOffset + bandSize * 3;

        float[] bandL = new float[height * pitch];
        float[] bandH = new float[height * pitch];

        float scale1 = tile.scales().getUnsigned(scaleOffset/**/);
        float scale2 = tile.scales().getUnsigned(scaleOffset + 1);
        float scale3 = tile.scales().getUnsigned(scaleOffset + 2);

        // Vertical
        for (int y = 0; y < height / 2; y++) {
            for (int x = 0; x < pitch; x++) {
                int i = y * pitch + x;
                int o = (y * 2) * pitch + x;

                float ll1 = unpackLow(src[ll + i + pitch]);
                float ll2 = unpackLow(src[ll + i + pitch * 2]);
                float ll3 = unpackLow(src[ll + i + pitch * 3]);
                float ll4 = unpackLow(src[ll + i + pitch * 4]);

                float lh0 = unpackHigh(src[lh + i]);
                float lh1 = unpackHigh(src[lh + i + pitch]);
                float lh2 = unpackHigh(src[lh + i + pitch * 2]);
                float lh3 = unpackHigh(src[lh + i + pitch * 3]);
                float lh4 = unpackHigh(src[lh + i + pitch * 4]);

                bandL[o] = lowEven(ll1, ll2, ll3) + highEven(lh0, lh1, lh2, lh3) * scale1;
                bandL[o + pitch] = lowOdd(ll1, ll2, ll3, ll4) + highOdd(lh0, lh1, lh2, lh3, lh4) * scale1;

                float hl1 = unpackHigh(src[hl + i + pitch]);
                float hl2 = unpackHigh(src[hl + i + pitch * 2]);
                float hl3 = unpackHigh(src[hl + i + pitch * 3]);
                float hl4 = unpackHigh(src[hl + i + pitch * 4]);

                float hh0 = unpackHigh(src[hh + i]);
                float hh1 = unpackHigh(src[hh + i + pitch]);
                float hh2 = unpackHigh(src[hh + i + pitch * 2]);
                float hh3 = unpackHigh(src[hh + i + pitch * 3]);
                float hh4 = unpackHigh(src[hh + i + pitch * 4]);

                bandH[o] = lowEven(hl1, hl2, hl3) * scale2 + highEven(hh0, hh1, hh2, hh3) * scale3;
                bandH[o + pitch] = lowOdd(hl1, hl2, hl3, hl4) * scale2 + highOdd(hh0, hh1, hh2, hh3, hh4) * scale3;
            }
        }

        // Horizontal
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                int i = y * pitch + x;

                float l1 = bandL[i + 1];
                float l2 = bandL[i + 2];
                float l3 = bandL[i + 3];
                float l4 = bandL[i + 4];

                float h0 = bandH[i/**/];
                float h1 = bandH[i + 1];
                float h2 = bandH[i + 2];
                float h3 = bandH[i + 3];
                float h4 = bandH[i + 4];

                float v0 = lowEven(l1, l2, l3) + highEven(h0, h1, h2, h3);
                float v1 = lowOdd(l1, l2, l3, l4) + highOdd(h0, h1, h2, h3, h4);

                int o = (y * width + x * 2) * stride + channel;
                dst[o] = FloatMath.packUNorm8(v0);
                dst[o + stride] = FloatMath.packUNorm8(v1);
            }
        }
    }

    private static int padded(int size) {
        return size / 2 + 4;
    }

    private static int subBandSize(int width, int height) {
        return padded(width) * padded(height);
    }

    private static float highEven(float h0, float h1, float h2, float h3) {
        return h0 * G1 + h1 * G3 + h2 * G3 + h3 * G1;
    }

    private static float highOdd(float h0, float h1, float h2, float h3, float h4) {
        return h0 * G0 + h1 * G2 + h2 * G4 + h3 * G2 + h4 * G0;
    }

    private static float lowEven(float l1, float l2, float l3) {
        return l1 * H2 + l2 * H4 + l3 * H2;
    }

    private static float lowOdd(float l1, float l2, float l3, float l4) {
        return l1 * H1 + l2 * H3 + l3 * H3 + l4 * H1;
    }

    private static float unpackLow(byte b) {
        return LO[Byte.toUnsignedInt(b)];
    }

    private static float unpackHigh(byte b) {
        return HI[Byte.toUnsignedInt(b)];
    }
}
