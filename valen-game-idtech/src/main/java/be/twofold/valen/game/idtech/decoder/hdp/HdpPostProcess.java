package be.twofold.valen.game.idtech.decoder.hdp;

import java.lang.invoke.*;
import java.nio.*;

/**
 * The output stage: reorders coefficients into pixels, then inverts the YCoCg-R colour transform to RGBA bytes.
 */
final class HdpPostProcess {
    private static final VarHandle PIXEL = MethodHandles
        .byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN)
        .withInvokeExactBehavior();

    private static final int[][] PIXEL_TO_SLOT = {
        {0x00, 0x01, 0x05, 0x04, 0x40, 0x41, 0x45, 0x44, 0x80, 0x81, 0x85, 0x84, 0xC0, 0xC1, 0xC5, 0xC4},
        {0x02, 0x03, 0x07, 0x06, 0x42, 0x43, 0x47, 0x46, 0x82, 0x83, 0x87, 0x86, 0xC2, 0xC3, 0xC7, 0xC6},
        {0x0A, 0x0B, 0x0F, 0x0E, 0x4A, 0x4B, 0x4F, 0x4E, 0x8A, 0x8B, 0x8F, 0x8E, 0xCA, 0xCB, 0xCF, 0xCE},
        {0x08, 0x09, 0x0D, 0x0C, 0x48, 0x49, 0x4D, 0x4C, 0x88, 0x89, 0x8D, 0x8C, 0xC8, 0xC9, 0xCD, 0xCC},
        {0x10, 0x11, 0x15, 0x14, 0x50, 0x51, 0x55, 0x54, 0x90, 0x91, 0x95, 0x94, 0xD0, 0xD1, 0xD5, 0xD4},
        {0x12, 0x13, 0x17, 0x16, 0x52, 0x53, 0x57, 0x56, 0x92, 0x93, 0x97, 0x96, 0xD2, 0xD3, 0xD7, 0xD6},
        {0x1A, 0x1B, 0x1F, 0x1E, 0x5A, 0x5B, 0x5F, 0x5E, 0x9A, 0x9B, 0x9F, 0x9E, 0xDA, 0xDB, 0xDF, 0xDE},
        {0x18, 0x19, 0x1D, 0x1C, 0x58, 0x59, 0x5D, 0x5C, 0x98, 0x99, 0x9D, 0x9C, 0xD8, 0xD9, 0xDD, 0xDC},
        {0x20, 0x21, 0x25, 0x24, 0x60, 0x61, 0x65, 0x64, 0xA0, 0xA1, 0xA5, 0xA4, 0xE0, 0xE1, 0xE5, 0xE4},
        {0x22, 0x23, 0x27, 0x26, 0x62, 0x63, 0x67, 0x66, 0xA2, 0xA3, 0xA7, 0xA6, 0xE2, 0xE3, 0xE7, 0xE6},
        {0x2A, 0x2B, 0x2F, 0x2E, 0x6A, 0x6B, 0x6F, 0x6E, 0xAA, 0xAB, 0xAF, 0xAE, 0xEA, 0xEB, 0xEF, 0xEE},
        {0x28, 0x29, 0x2D, 0x2C, 0x68, 0x69, 0x6D, 0x6C, 0xA8, 0xA9, 0xAD, 0xAC, 0xE8, 0xE9, 0xED, 0xEC},
        {0x30, 0x31, 0x35, 0x34, 0x70, 0x71, 0x75, 0x74, 0xB0, 0xB1, 0xB5, 0xB4, 0xF0, 0xF1, 0xF5, 0xF4},
        {0x32, 0x33, 0x37, 0x36, 0x72, 0x73, 0x77, 0x76, 0xB2, 0xB3, 0xB7, 0xB6, 0xF2, 0xF3, 0xF7, 0xF6},
        {0x3A, 0x3B, 0x3F, 0x3E, 0x7A, 0x7B, 0x7F, 0x7E, 0xBA, 0xBB, 0xBF, 0xBE, 0xFA, 0xFB, 0xFF, 0xFE},
        {0x38, 0x39, 0x3D, 0x3C, 0x78, 0x79, 0x7D, 0x7C, 0xB8, 0xB9, 0xBD, 0xBC, 0xF8, 0xF9, 0xFD, 0xFC},
    };

    // Rounding
    static final int SCALED_ARITH_SHIFT = 3;
    static final int BIAS = (128 << SCALED_ARITH_SHIFT) + ((1 << (SCALED_ARITH_SHIFT - 1)) - 1);

    private HdpPostProcess() {
    }

    /**
     * Reorder MB-major slots into row-major pixels, output channel-major.
     */
    static short[] unZigzag(
        int[] decodedSlots, int numChannels, int accumChannelStride,
        int mbW, int mbH, int width, int height
    ) {
        int pixelChannelStride = height * width;
        short[] pixels = new short[numChannels * pixelChannelStride];
        int rowSize = mbW * HdpConstants.MB_SIZE;
        for (int mbY = 0; mbY < mbH; mbY++) {
            int rowBase = mbY * rowSize;
            int pixelBaseY = mbY * 16;
            for (int mbX = 0; mbX < mbW; mbX++) {
                int mbBase = rowBase + mbX * HdpConstants.MB_SIZE;
                int pixelBaseX = mbX * 16;
                for (int row = 0; row < 16; row++) {
                    int pixelRow = pixelBaseY + row;
                    if (pixelRow >= height) {
                        break;
                    }
                    int pixelOffset = pixelRow * width + pixelBaseX;
                    int[] rowMap = PIXEL_TO_SLOT[row];
                    int colLimit = Math.min(16, width - pixelBaseX);
                    for (int ch = 0; ch < numChannels; ch++) {
                        int slotsBase = ch * accumChannelStride + mbBase;
                        int pixelsBase = ch * pixelChannelStride + pixelOffset;
                        for (int col = 0; col < colLimit; col++) {
                            pixels[pixelsBase + col] = (short) decodedSlots[slotsBase + rowMap[col]];
                        }
                    }
                }
            }
        }
        return pixels;
    }

    /**
     * Three or more channels take the YCoCg-R inverse with a fourth supplying alpha; fewer broadcast channel 0 to grey.
     */
    static byte[] toRgba(short[] pixels, int numChannels, int width, int height) {
        int numPixels = width * height;
        byte[] image = new byte[numPixels * 4];

        for (int i = 0; i < numPixels; i++) {
            int rgba;
            if (numChannels >= 3) {
                int y = pixels[i];
                int u = pixels[i + numPixels];
                int v = pixels[i + numPixels * 2];
                int rgb = yCoCgToRgb(y, u, v);
                int a = numChannels >= 4 ? alphaSampleToByte(pixels[i + numPixels * 3]) : 0xFF;
                rgba = rgb | a << 24;
            } else {
                int y = alphaSampleToByte(pixels[i]);
                int a = numChannels == 2 ? alphaSampleToByte(pixels[i + numPixels]) : 0xFF;
                rgba = y * 0x010101 | a << 24;
            }
            PIXEL.set(image, i * 4, rgba);
        }
        return image;
    }

    static int yCoCgToRgb(int y, int u, int v) {
        int g = y + BIAS;
        int r = -u;
        int b = v;
        g -= r >> 1;
        r -= ((b + 1) >> 1) - g;
        b += r;
        g >>= SCALED_ARITH_SHIFT;
        r >>= SCALED_ARITH_SHIFT;
        b >>= SCALED_ARITH_SHIFT;
        return (clip8(b) << 16) | (clip8(g) << 8) | clip8(r);
    }

    static int alphaSampleToByte(int sample) {
        return clip8((sample + BIAS) >> SCALED_ARITH_SHIFT);
    }

    private static int clip8(int v) {
        return Math.clamp(v, 0, 255);
    }
}
