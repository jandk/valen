package be.twofold.valen.game.colossus.reader.image;

import wtf.reversed.toolbox.math.*;

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

    private WbpDecoder() {
    }

    public static byte[] decode(ImageTile tile, byte[] data) {
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

    private static void decodeTile(
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

        int subBandWidth = width >> 2 << 1;
        int subBandHeight = height >> 2 << 1;
        int pitch = subBandWidth + 4;
        int subBandSize = (subBandHeight + 4) * pitch;

        int ll = srcOffset;
        int hl = srcOffset + subBandSize;
        int lh = srcOffset + subBandSize * 2;
        int hh = srcOffset + subBandSize * 3;

        float[] bandL = new float[subBandHeight * 2 * pitch];
        float[] bandH = new float[subBandHeight * 2 * pitch];

        float scale1 = tile.scales().getUnsigned(scaleOffset/**/);
        float scale2 = tile.scales().getUnsigned(scaleOffset + 1);
        float scale3 = tile.scales().getUnsigned(scaleOffset + 2);

        // Vertical
        for (int y = 0; y < subBandHeight; y++) {
            for (int x = 0; x < pitch; x++) {
                int i = y * pitch + x;
                int o = (y * 2) * pitch + x;

                float ll1 = unpackLow(src[ll + i + pitch]);
                float ll2 = unpackLow(src[ll + i + pitch * 2]);
                float ll3 = unpackLow(src[ll + i + pitch * 3]);
                float ll4 = unpackLow(src[ll + i + pitch * 4]);

                float lh0 = unpackHigh(src[lh + i]) * scale1;
                float lh1 = unpackHigh(src[lh + i + pitch]) * scale1;
                float lh2 = unpackHigh(src[lh + i + pitch * 2]) * scale1;
                float lh3 = unpackHigh(src[lh + i + pitch * 3]) * scale1;
                float lh4 = unpackHigh(src[lh + i + pitch * 4]) * scale1;

                bandL[o] = f1(ll1, ll2, ll3, lh0, lh1, lh2, lh3);
                bandL[o + pitch] = f2(ll1, ll2, ll3, ll4, lh0, lh1, lh2, lh3, lh4);

                float hl1 = unpackHigh(src[hl + i + pitch]) * scale2;
                float hl2 = unpackHigh(src[hl + i + pitch * 2]) * scale2;
                float hl3 = unpackHigh(src[hl + i + pitch * 3]) * scale2;
                float hl4 = unpackHigh(src[hl + i + pitch * 4]) * scale2;

                float hh0 = unpackHigh(src[hh + i]) * scale3;
                float hh1 = unpackHigh(src[hh + i + pitch]) * scale3;
                float hh2 = unpackHigh(src[hh + i + pitch * 2]) * scale3;
                float hh3 = unpackHigh(src[hh + i + pitch * 3]) * scale3;
                float hh4 = unpackHigh(src[hh + i + pitch * 4]) * scale3;

                bandH[o] = f1(hl1, hl2, hl3, hh0, hh1, hh2, hh3);
                bandH[o + pitch] = f2(hl1, hl2, hl3, hl4, hh0, hh1, hh2, hh3, hh4);
            }
        }

        for (int y = 0; y < subBandHeight * 2; y++) {
            for (int x = 0; x < subBandWidth; x++) {
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

                float v0 = f1(l1, l2, l3, h0, h1, h2, h3);
                float v1 = f2(l1, l2, l3, l4, h0, h1, h2, h3, h4);

                int o = (y * width + x * 2) * stride + channel;
                dst[o] = FloatMath.packUNorm8(v0);
                dst[o + stride] = FloatMath.packUNorm8(v1);
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
        return FloatMath.unpackUNorm8(b);
    }

    private static float unpackHigh(byte b) {
        return Byte.toUnsignedInt(b) * (2.0f / 254.0f) - 1.0f;
    }

}
