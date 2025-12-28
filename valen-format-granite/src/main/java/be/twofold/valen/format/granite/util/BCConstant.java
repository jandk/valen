package be.twofold.valen.format.granite.util;

import wtf.reversed.toolbox.collect.*;

import java.util.stream.*;

public final class BCConstant {
    private static final EndpointPair[] TABLE_5 = buildBC1Table(5);
    private static final EndpointPair[] TABLE_6 = buildBC1Table(6);

    private static EndpointPair[] buildBC1Table(int bits) {
        return IntStream.range(0, 256)
            .mapToObj(i -> findBestColor(i, bits))
            .toArray(EndpointPair[]::new);
    }

    public static Bytes bc1(byte r, byte g, byte b) {
        Bytes.Mutable bytes = Bytes.Mutable.allocate(8);
        bc1(bytes, 0, r, g, b);
        return bytes;
    }

    public static Bytes bc3(byte r, byte g, byte b, byte a) {
        Bytes.Mutable bytes = Bytes.Mutable.allocate(16);
        bc4(bytes, 0, a);
        bc1(bytes, 8, r, g, b);
        return bytes;
    }

    public static Bytes bc4(byte a) {
        Bytes.Mutable bytes = Bytes.Mutable.allocate(8);
        bc4(bytes, 0, a);
        return bytes;
    }

    public static Bytes bc5(byte r, byte g) {
        Bytes.Mutable bytes = Bytes.Mutable.allocate(16);
        bc4(bytes, 0, r);
        bc4(bytes, 8, g);
        return bytes;
    }

    // https://fgiesen.wordpress.com/2024/11/03/bc7-optimal-solid-color-blocks/
    public static Bytes bc7(byte r, byte g, byte b, byte a) {
        long lo = bc7AlphaPair(a) << 50;
        lo = lo | bc7ColorPair(b) << 36;
        lo = lo | bc7ColorPair(g) << 22;
        lo = lo | bc7ColorPair(r) << 8;
        lo = lo | 0x20; // Mode 5, no rotation

        long hi = (long) 0x2AAAAAAB << 2;
        hi = hi | (a >>> 6) & 0x03;

        return Bytes.Mutable
            .allocate(16)
            .setLong(0, lo)
            .setLong(8, hi);
    }

    private static long bc7ColorPair(byte c) {
        int cc = Byte.toUnsignedInt(c);
        int c0 = cc >>> 1;
        int c1 = (cc < 128 ? cc + 1 : cc - 1) >>> 1;
        return c0 | c1 << 7;
    }

    private static long bc7AlphaPair(byte a) {
        return Byte.toUnsignedInt(a) * 0x0101;
    }

    private static void bc1(Bytes.Mutable bytes, int offset, byte r, byte g, byte b) {
        int ri = Byte.toUnsignedInt(r);
        int gi = Byte.toUnsignedInt(g);
        int bi = Byte.toUnsignedInt(b);

        int c0 = TABLE_5[ri].c0 << 11 | TABLE_6[gi].c0 << 5 | TABLE_5[bi].c0;
        int c1 = TABLE_5[ri].c1 << 11 | TABLE_6[gi].c1 << 5 | TABLE_5[bi].c1;
        int bits = 0xAAAAAAAA;

        if (c0 < c1) {
            int ct = c0;
            c0 = c1;
            c1 = ct;
            bits = 0xFFFFFFFF;
        } else if (c0 == c1) {
            bits = 0x00000000;
        }

        bytes
            .setShort(offset/**/, (short) c0)
            .setShort(offset + 2, (short) c1)
            .setInt(offset + 4, bits);
    }

    private static void bc4(Bytes.Mutable bytes, int offset, byte a) {
        bytes
            .set(offset/**/, a)
            .set(offset + 1, a)
            .slice(2, 8).fill((byte) 0);
    }

    // region Table Generation

    private static EndpointPair findBestColor(int i, int bits) {
        int max = 1 << bits;
        int bestA = 255, bestB = 255, bestE = 255;
        for (int a = 0; a < max; a++) {
            int ae = bits == 5 ? expand5(a) : expand6(a);
            for (int b = 0; b < max; b++) {
                int be = bits == 5 ? expand5(b) : expand6(b);
                int v = interp(ae, be);

                int e = Math.abs(v - i);
                if (e < bestE || e == bestE && a == b) {
                    bestE = e;
                    bestA = a;
                    bestB = b;
                }
            }
        }
        return new EndpointPair((byte) bestA, (byte) bestB);
    }

    private static int expand5(int a) {
        return (a << 3) | (a >> 2);
    }

    private static int expand6(int a) {
        return (a << 2) | (a >> 4);
    }

    private static int interp(int ae, int be) {
        return ((171 * ae) + (85 * be) + 128) >> 8;
    }

    private record EndpointPair(byte c0, byte c1) {
    }

    // endregion

}
