package be.twofold.valen.core.texture.decoder;

import java.lang.invoke.*;
import java.nio.*;

final class BcUtils {
    static final VarHandle ShortVarHandle =
        MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN)
            .withInvokeExactBehavior();
    static final VarHandle IntVarHandle =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN)
            .withInvokeExactBehavior();
    static final VarHandle LongVarHandle =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN)
            .withInvokeExactBehavior();

    private BcUtils() {
    }

    static void colorBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int stride, boolean opaque) {
        int c0 = Short.toUnsignedInt((short) ShortVarHandle.get(src, srcPos));
        int c1 = Short.toUnsignedInt((short) ShortVarHandle.get(src, srcPos + 2));
        int bits = (int) IntVarHandle.get(src, srcPos + 4);

        int r0 = expand5to8((c0 >>> 11) & 0x1f);
        int g0 = expand6to8((c0 >>> 5) & 0x3f);
        int b0 = expand5to8((c0) & 0x1f);

        int r1 = expand5to8((c1 >>> 11) & 0x1f);
        int g1 = expand6to8((c1 >>> 5) & 0x3f);
        int b1 = expand5to8((c1) & 0x1f);

        int[] colors = new int[4];
        colors[0] = rgb(r0, g0, b0);
        colors[1] = rgb(r1, g1, b1);

        if (c0 > c1 || opaque) {
            int r2 = ((r0 << 1) + r1 + 1) / 3;
            int g2 = ((g0 << 1) + g1 + 1) / 3;
            int b2 = ((b0 << 1) + b1 + 1) / 3;
            colors[2] = rgb(r2, g2, b2);

            int r3 = (r0 + (r1 << 1) + 1) / 3;
            int g3 = (g0 + (g1 << 1) + 1) / 3;
            int b3 = (b0 + (b1 << 1) + 1) / 3;
            colors[3] = rgb(r3, g3, b3);
        } else {
            int r2 = (r0 + r1) >>> 1;
            int g2 = (g0 + g1) >>> 1;
            int b2 = (b0 + b1) >>> 1;
            colors[2] = rgb(r2, g2, b2);
        }

        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 2) {
                IntVarHandle.set(dst, dstPos, colors[(bits >>> shift) & 3]);
                dstPos += 4;
            }
            dstPos += stride - 16;
        }
    }

    static void alphaBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int stride, int size) {
        long block = (long) LongVarHandle.get(src, srcPos);
        int a0 = (int) (block & 0xff);
        int a1 = (int) ((block >>> 8) & 0xff);
        block >>>= 16;

        byte[] alphas = new byte[8];
        alphas[0] = (byte) a0;
        alphas[1] = (byte) a1;

        if (a0 > a1) {
            alphas[2] = (byte) ((6 * a0 + 1 * a1 + 3) / 7);
            alphas[3] = (byte) ((5 * a0 + 2 * a1 + 3) / 7);
            alphas[4] = (byte) ((4 * a0 + 3 * a1 + 3) / 7);
            alphas[5] = (byte) ((3 * a0 + 4 * a1 + 3) / 7);
            alphas[6] = (byte) ((2 * a0 + 5 * a1 + 3) / 7);
            alphas[7] = (byte) ((1 * a0 + 6 * a1 + 3) / 7);
        } else {
            alphas[2] = (byte) ((4 * a0 + 1 * a1 + 2) / 5);
            alphas[3] = (byte) ((3 * a0 + 2 * a1 + 2) / 5);
            alphas[4] = (byte) ((2 * a0 + 3 * a1 + 2) / 5);
            alphas[5] = (byte) ((1 * a0 + 4 * a1 + 2) / 5);
            alphas[6] = (byte) 0x00;
            alphas[7] = (byte) 0xff;
        }

        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 3) {
                int colorIndex = (int) ((block >>> shift) & 7);
                dst[dstPos] = alphas[colorIndex];
                dstPos += size;
            }
            dstPos += stride - 4 * size;
        }
    }

    static void alphaBlock16(byte[] src, int srcPos, byte[] dst, int dstPos, int stride) {
        long block = (long) LongVarHandle.get(src, srcPos);

        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 4) {
                dst[dstPos] = (byte) (((block >>> shift) & 0x0f) * 0x11);
                dstPos += 4;
            }
            dstPos += stride - 16;
        }
    }

    private static int expand5to8(int value) {
        return (value * 527 + 23) >>> 6;
    }

    private static int expand6to8(int value) {
        return (value * 259 + 33) >>> 6;
    }

    private static int rgb(int r, int g, int b) {
        return 0xff000000 | (b << 16) | (g << 8) | r;
    }
}
