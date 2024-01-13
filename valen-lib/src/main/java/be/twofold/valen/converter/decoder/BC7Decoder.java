package be.twofold.valen.converter.decoder;

import java.util.*;

public final class BC7Decoder extends BCDecoder {
    private static final int[] SUBSET2 = {
        0x50505050, 0x40404040, 0x54545454, 0x54505040, 0x50404000, 0x55545450, 0x55545040, 0x54504000,
        0x50400000, 0x55555450, 0x55544000, 0x54400000, 0x55555440, 0x55550000, 0x55555500, 0x55000000,
        0x55150100, 0x00004054, 0x15010000, 0x00405054, 0x00004050, 0x15050100, 0x05010000, 0x40505054,
        0x00404050, 0x05010100, 0x14141414, 0x05141450, 0x01155440, 0x00555500, 0x15014054, 0x05414150,
        0x44444444, 0x55005500, 0x11441144, 0x05055050, 0x05500550, 0x11114444, 0x41144114, 0x44111144,
        0x15055054, 0x01055040, 0x05041050, 0x05455150, 0x14414114, 0x50050550, 0x41411414, 0x00141400,
        0x00041504, 0x00105410, 0x10541000, 0x04150400, 0x50410514, 0x41051450, 0x05415014, 0x14054150,
        0x41050514, 0x41505014, 0x40011554, 0x54150140, 0x50505500, 0x00555050, 0x15151010, 0x54540404,
    };

    private static final int[] SUBSET3 = {
        0xaa685050, 0x6a5a5040, 0x5a5a4200, 0x5450a0a8, 0xa5a50000, 0xa0a05050, 0x5555a0a0, 0x5a5a5050,
        0xaa550000, 0xaa555500, 0xaaaa5500, 0x90909090, 0x94949494, 0xa4a4a4a4, 0xa9a59450, 0x2a0a4250,
        0xa5945040, 0x0a425054, 0xa5a5a500, 0x55a0a0a0, 0xa8a85454, 0x6a6a4040, 0xa4a45000, 0x1a1a0500,
        0x0050a4a4, 0xaaa59090, 0x14696914, 0x69691400, 0xa08585a0, 0xaa821414, 0x50a4a450, 0x6a5a0200,
        0xa9a58000, 0x5090a0a8, 0xa8a09050, 0x24242424, 0x00aa5500, 0x24924924, 0x24499224, 0x50a50a50,
        0x500aa550, 0xaaaa4444, 0x66660000, 0xa5a0a5a0, 0x50a050a0, 0x69286928, 0x44aaaa44, 0x66666600,
        0xaa444444, 0x54a854a8, 0x95809580, 0x96969600, 0xa85454a8, 0x80959580, 0xaa141414, 0x96960000,
        0xaaaa1414, 0xa05050a0, 0xa0a5a5a0, 0x96000000, 0x40804080, 0xa9a8a9a8, 0xaaaaaa44, 0x2a4a5254,
    };

    private static final int[] ANCHOR_11 = {
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, +2, +8, +2, +2, +8, +8, 15,
        +2, +8, +2, +2, +8, +8, +2, +2,
        15, 15, +6, +8, +2, +8, 15, 15,
        +2, +8, +2, +2, +2, 15, 15, +6,
        +6, +2, +6, +8, 15, 15, +2, +2,
        15, 15, 15, 15, 15, +2, +2, 15,
    };

    private static final int[] ANCHOR_21 = {
        +3, +3, 15, 15, +8, +3, 15, 15,
        +8, +8, +6, +6, +6, +5, +3, +3,
        +3, +3, +8, 15, +3, +3, +6, 10,
        +5, +8, +8, +6, +8, +5, 15, 15,
        +8, 15, +3, +5, +6, 10, +8, 15,
        15, +3, 15, +5, 15, 15, 15, 15,
        +3, 15, +5, +5, +5, +8, +5, 10,
        +5, 10, +8, 13, 15, 12, +3, +3,
    };

    private static final int[] ANCHOR_22 = {
        15, +8, +8, +3, 15, 15, +3, +8,
        15, 15, 15, 15, 15, 15, 15, +8,
        15, +8, 15, +3, 15, +8, 15, +8,
        +3, 15, +6, 10, 15, 15, 10, +8,
        15, +3, 15, 10, 10, +8, +9, 10,
        +6, 15, +8, 15, +3, +6, +6, +8,
        15, +3, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, +3, 15, 15, +8,
    };

    private static final int[][] WEIGHTS = {
        {},
        {},
        {0, 21, 43, 64},
        {0, 9, 18, 27, 37, 46, 55, 64},
        {0, 4, 9, 13, 17, 21, 26, 30, 34, 38, 43, 47, 51, 55, 60, 64}
    };

    private static final List<Mode> MODES = List.of(
        new Mode(3, 4, 0, false, 4, 0, true, false, +3, 0),
        new Mode(2, 6, 0, false, 6, 0, false, true, +3, 0),
        new Mode(3, 6, 0, false, 5, 0, false, false, 2, 0),
        new Mode(2, 6, 0, false, 7, 0, true, false, +2, 0),
        new Mode(1, 0, 2, true, +5, 6, false, false, 2, 3),
        new Mode(1, 0, 2, false, 7, 8, false, false, 2, 2),
        new Mode(1, 0, 0, false, 7, 7, true, false, +4, 0),
        new Mode(2, 6, 0, false, 5, 5, true, false, +2, 0)
    );

    public BC7Decoder() {
        super(16, 4);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        Bits bits = new Bits(src, srcPos);
        Mode mode = MODES.get(mode(bits));
        int partition = bits.getBits(mode.pb());
        int rotation = bits.getBits(mode.rb());
        boolean selection = mode.isb() && bits.getBit() != 0;

        int[][] colors = new int[mode.ns() * 2][4];

        // Read colors
        for (int c = 0; c < 3; c++) {
            for (int[] color : colors) {
                color[c] = bits.getBits(mode.cb());
            }
        }

        // Read alphas
        if (mode.ab() != 0) {
            for (int[] color : colors) {
                color[3] = bits.getBits(mode.ab());
            }
        }

        // Read endpoint p-bits
        if (mode.epb()) {
            for (int[] color : colors) {
                int pBit = bits.getBit();
                for (int c = 0; c < 4; c++) {
                    color[c] = (color[c] << 1) | pBit;
                }
            }
        }

        // Read shared p-bits
        if (mode.spb()) {
            int sBit1 = bits.getBit();
            int sBit2 = bits.getBit();
            for (int c = 0; c < 4; c++) {
                colors[0][c] = (colors[0][c] << 1) | sBit1;
                colors[1][c] = (colors[1][c] << 1) | sBit1;
                colors[2][c] = (colors[2][c] << 1) | sBit2;
                colors[3][c] = (colors[3][c] << 1) | sBit2;
            }
        }

        // Unpack colors
        int colorBits = mode.cb() + (mode.epb() ? 1 : 0) + (mode.spb() ? 1 : 0);
        int alphaBits = mode.ab() + (mode.epb() ? 1 : 0) + (mode.spb() ? 1 : 0);
        for (int[] color : colors) {
            if (colorBits < 8) {
                color[0] = unpack(color[0], colorBits);
                color[1] = unpack(color[1], colorBits);
                color[2] = unpack(color[2], colorBits);
            }
            if (mode.ab() != 0 && alphaBits < 8) {
                color[3] = unpack(color[3], alphaBits);
            }
        }

        // Opaque mode
        if (mode.ab() == 0) {
            for (int[] color : colors) {
                color[3] = 0xff;
            }
        }


        int a1 = 0;
        int a2 = 0;
        int partitionTable = 0;
        if (mode.ns() == 2) {
            a1 = ANCHOR_11[partition];
            partitionTable = SUBSET2[partition];
        } else if (mode.ns() == 3) {
            a1 = ANCHOR_21[partition];
            a2 = ANCHOR_22[partition];
            partitionTable = SUBSET3[partition];
        }

        // Interleaving would have been so much nicer...
        int[] indexBits = new int[16];
        for (int i = 0; i < 16; i++) {
            boolean anchored = i == 0 || i == a1 || i == a2;
            int numBits = mode.ib1() - (anchored ? 1 : 0);
            indexBits[i] = bits.getBits(numBits);
        }

        int[] weights1 = WEIGHTS[mode.ib1()];
        int[] weights2 = WEIGHTS[mode.ib2()];
        for (int y = 0, i = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, i++) {
                int index1 = indexBits[i];
                int cWeight = weights1[index1];
                int aWeight = weights1[index1];

                if (mode.ib2() != 0) {
                    boolean anchored = i == 0 || i == a1 || i == a2;
                    int numBits = mode.ib2() - (anchored ? 1 : 0);
                    int index2 = bits.getBits(numBits);

                    if (selection) {
                        cWeight = weights2[index2];
                    } else {
                        aWeight = weights2[index2];
                    }
                }

                int pIndex = partitionTable >>> (i * 2) & 3;
                int[] c0 = colors[pIndex * 2];
                int[] c1 = colors[pIndex * 2 + 1];

                dst[dstPos + 0] = (byte) interpolate(c0[0], c1[0], cWeight);
                dst[dstPos + 1] = (byte) interpolate(c0[1], c1[1], cWeight);
                dst[dstPos + 2] = (byte) interpolate(c0[2], c1[2], cWeight);
                dst[dstPos + 3] = (byte) interpolate(c0[3], c1[3], aWeight);

                if (rotation != 0) {
                    swap(dst, dstPos + 3, dstPos + rotation - 1);
                }
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }

    private int interpolate(int e0, int e1, int weight) {
        return (e0 * (64 - weight) + e1 * weight + 32) >>> 6;
    }

    private int unpack(int i, int n) {
        assert n >= 4 && n <= 8;
        return i << (8 - n) | i >> (2 * n - 8);
    }

    private void swap(byte[] array, int a, int b) {
        byte temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    private int mode(Bits bits) {
        int mode = 0;
        while (true) {
            if (bits.getBit() != 0) {
                return mode;
            }
            mode++;
        }
    }

    private record Mode(
        int ns,
        int pb,
        int rb,
        boolean isb,
        int cb,
        int ab,
        boolean epb,
        boolean spb,
        int ib1,
        int ib2
    ) {
    }

    private static final class Bits {
        private final byte[] array;
        private int index;

        private int bitBuffer = 0;
        private int bitCount = 0;

        private Bits(byte[] array, int index) {
            this.array = array;
            this.index = index;
        }

        void refill(int count) {
            assert count >= 0 && count <= (32 - 7);
            while (bitCount < count) {
                bitBuffer |= Byte.toUnsignedInt(array[index++]) << bitCount;
                bitCount += 8;
            }
        }

        int peekBits(int count) {
            assert bitCount >= count;
            return bitBuffer & ((1 << count) - 1);
        }

        void consume(int count) {
            assert bitCount >= count;
            bitBuffer >>>= count;
            bitCount -= count;
        }

        int getBits(int count) {
            refill(count);
            int result = peekBits(count);
            consume(count);
            return result;
        }

        int getBit() {
            return getBits(1);
        }
    }
}
