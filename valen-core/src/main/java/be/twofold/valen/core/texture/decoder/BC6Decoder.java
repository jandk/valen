package be.twofold.valen.core.texture.decoder;

import java.util.*;

public final class BC6Decoder extends BCDecoder {
    private static final List<Mode> MODES = List.of(
        new Mode(true, +5, 10, +5, +5, +5, new short[]{0x0147, 0x0148, 0x014B, 0x0A00, 0x0A01, 0x0A02, 0x0503, 0x014A, 0x0407, 0x0504, 0x010B, 0x040A, 0x0505, 0x011B, 0x0408, 0x0506, 0x012B, 0x0509, 0x013B}),
        new Mode(true, +5, +7, +6, +6, +6, new short[]{0x0157, 0x014A, 0x015A, 0x0700, 0x010B, 0x011B, 0x0148, 0x0701, 0x0158, 0x012B, 0x0147, 0x0702, 0x013B, 0x015B, 0x014B, 0x0603, 0x0407, 0x0604, 0x040A, 0x0605, 0x0408, 0x0606, 0x0609}),
        new Mode(true, +5, 11, +5, +4, +4, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0503, 0x01A0, 0x0407, 0x0404, 0x01A1, 0x010B, 0x040A, 0x0405, 0x01A2, 0x011B, 0x0408, 0x0506, 0x012B, 0x0509, 0x013B}),
        new Mode(true, +5, 11, +4, +5, +4, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0403, 0x01A0, 0x014A, 0x0407, 0x0504, 0x01A1, 0x040A, 0x0405, 0x01A2, 0x011B, 0x0408, 0x0406, 0x010B, 0x012B, 0x0409, 0x0147, 0x013B}),
        new Mode(true, +5, 11, +4, +4, +5, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0403, 0x01A0, 0x0148, 0x0407, 0x0404, 0x01A1, 0x010B, 0x040A, 0x0505, 0x01A2, 0x0408, 0x0406, 0x011B, 0x012B, 0x0409, 0x014B, 0x013B}),
        new Mode(true, +5, +9, +5, +5, +5, new short[]{0x0900, 0x0148, 0x0901, 0x0147, 0x0902, 0x014B, 0x0503, 0x014A, 0x0407, 0x0504, 0x010B, 0x040A, 0x0505, 0x011B, 0x0408, 0x0506, 0x012B, 0x0509, 0x013B}),
        new Mode(true, +5, +8, +6, +5, +5, new short[]{0x0800, 0x014A, 0x0148, 0x0801, 0x012B, 0x0147, 0x0802, 0x013B, 0x014B, 0x0603, 0x0407, 0x0504, 0x010B, 0x040A, 0x0505, 0x011B, 0x0408, 0x0606, 0x0609}),
        new Mode(true, +5, +8, +5, +6, +5, new short[]{0x0800, 0x010B, 0x0148, 0x0801, 0x0157, 0x0147, 0x0802, 0x015A, 0x014B, 0x0503, 0x014A, 0x0407, 0x0604, 0x040A, 0x0505, 0x011B, 0x0408, 0x0506, 0x012B, 0x0509, 0x013B}),
        new Mode(true, +5, +8, +5, +5, +6, new short[]{0x0800, 0x011B, 0x0148, 0x0801, 0x0158, 0x0147, 0x0802, 0x015B, 0x014B, 0x0503, 0x014A, 0x0407, 0x0504, 0x010B, 0x040A, 0x0605, 0x0408, 0x0506, 0x012B, 0x0509, 0x013B}),
        new Mode(false, 5, +6, +6, +6, +6, new short[]{0x0600, 0x014A, 0x010B, 0x011B, 0x0148, 0x0601, 0x0157, 0x0158, 0x012B, 0x0147, 0x0602, 0x015A, 0x013B, 0x015B, 0x014B, 0x0603, 0x0407, 0x0604, 0x040A, 0x0605, 0x0408, 0x0606, 0x0609}),
        new Mode(false, 0, 10, 10, 10, 10, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0A03, 0x0A04, 0x0A05}),
        new Mode(true, +0, 11, +9, +9, +9, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0903, 0x01A0, 0x0904, 0x01A1, 0x0905, 0x01A2}),
        new Mode(true, +0, 12, +8, +8, +8, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0803, 0x12A0, 0x0804, 0x12A1, 0x0805, 0x12A2}),
        new Mode(true, +0, 16, +4, +4, +4, new short[]{0x0A00, 0x0A01, 0x0A02, 0x0403, 0x16A0, 0x0404, 0x16A1, 0x0405, 0x16A2})
    );

    private final boolean signed;

    BC6Decoder(boolean signed) {
        super(16, 6);
        this.signed = signed;
    }

    public final Map<Integer, Integer> modes = new HashMap<>();

    @Override
    @SuppressWarnings("PointlessArithmeticExpression")
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        Bits bits = Bits.from(src, srcPos);
        int mode = mode(bits);
        Mode info = MODES.get(mode);

        modes.merge(mode, 1, Integer::sum);

        int[] colors = new int[12];
        for (short op : info.ops()) {
            readOp(bits, op, colors);
        }

        int partition = bits.getBits(info.pb());
        int numPartitions = info.pb() != 0 ? 2 : 1;

        // The values in E0 are sign-extended to the implementation’s internal integer representation if
        // the format of the texture is signed
        if (signed) {
            colors[0] = extendSign(colors[0], info.epb());
            colors[1] = extendSign(colors[1], info.epb());
            colors[2] = extendSign(colors[2], info.epb());
        }

        boolean transformedEndpoints = mode != 9 && mode != 10;
        if (signed || transformedEndpoints) {
            for (int i = 3; i < numPartitions * 6; i += 3) {
                colors[i + 0] = extendSign(colors[i + 0], info.rb());
                colors[i + 1] = extendSign(colors[i + 1], info.gb());
                colors[i + 2] = extendSign(colors[i + 2], info.bb());
            }
        }

        if (transformedEndpoints) {
            for (int i = 3; i < numPartitions * 6; i += 3) {
                colors[i + 0] = transformInverse(colors[i + 0], colors[0], info.epb(), signed);
                colors[i + 1] = transformInverse(colors[i + 1], colors[1], info.epb(), signed);
                colors[i + 2] = transformInverse(colors[i + 2], colors[2], info.epb(), signed);
            }
        }

        for (int i = 0; i < numPartitions * 6; i += 3) {
            colors[i + 0] = unquantize(colors[i + 0], info.epb(), signed);
            colors[i + 1] = unquantize(colors[i + 1], info.epb(), signed);
            colors[i + 2] = unquantize(colors[i + 2], info.epb(), signed);
        }


        int ib = 4;
        int anchor = 0;
        int partitionTable = 0;
        if (numPartitions == 2) {
            ib = 3;
            anchor = BC7Decoder.ANCHOR_11[partition];
            partitionTable = BC7Decoder.SUBSET2[partition];
        }

        // Interleaving would have been so much nicer...
        int[] indexBits = new int[16];
        for (int i = 0; i < 16; i++) {
            boolean anchored = i == 0 || i == anchor;
            int numBits = ib - (anchored ? 1 : 0);
            indexBits[i] = bits.getBits(numBits);
        }

        int[] weights1 = BC7Decoder.WEIGHTS[ib];
        for (int y = 0, i = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, i++) {
                int pIndex = partitionTable >>> (i * 2) & 3;
                int ci0 = pIndex * 2 * 3;
                int ci1 = ci0 + 3;

                int ra = colors[ci0 + 0];
                int ga = colors[ci0 + 1];
                int ba = colors[ci0 + 2];
                int rb = colors[ci1 + 0];
                int gb = colors[ci1 + 1];
                int bb = colors[ci1 + 2];

                int weight = weights1[indexBits[i]];
                short r = (short) finalUnquantize(BC7Decoder.interpolate(ra, rb, weight), signed);
                short g = (short) finalUnquantize(BC7Decoder.interpolate(ga, gb, weight), signed);
                short b = (short) finalUnquantize(BC7Decoder.interpolate(ba, bb, weight), signed);

                BcUtils.ShortVarHandle.set(dst, dstPos + 0, r);
                BcUtils.ShortVarHandle.set(dst, dstPos + 2, g);
                BcUtils.ShortVarHandle.set(dst, dstPos + 4, b);

                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }

    private void readOp(Bits bits, short op, int[] colors) {
        // index | shift << 4 | count << 8 | (reverse ? 1 : 0) << 12
        int index = op & 0x0F;
        int shift = (op >>> 4) & 0x0F;
        int count = (op >>> 8) & 0x0F;
        boolean reverse = (op >>> 12) != 0;

        int value = bits.getBits(count);
        if (reverse) {
            value = Integer.reverse(value) >>> (32 - count);
        }
        colors[index] |= value << shift;
    }

    private int mode(Bits bits) {
        int mode = bits.getBits(2);
        return switch (mode) {
            case 0, 1 -> mode;
            case 2 -> bits.getBits(3) + 2;
            case 3 -> bits.getBits(3) + 10;
            default -> throw new UnsupportedOperationException();
        };
    }

    private int unquantize(int value, int bits, boolean signed) {
        if (signed) {
            if (bits >= 16 || value == 0) {
                return value;
            }

            boolean sign;
            if (value < 0) {
                value = -value;
                sign = true;
            } else {
                sign = false;
            }

            int unq;
            if (value >= ((1 << (bits - 1)) - 1)) {
                unq = 0x7FFF;
            } else {
                unq = ((value << 15) + 0x4000) >> (bits - 1);
            }
            return sign ? -unq : unq;
        } else {
            if (bits >= 15 || value == 0) {
                return value;
            }
            if (value == ((1 << bits) - 1)) {
                return 0xFFFF;
            }
            return ((value << 15) + 0x4000) >> (bits - 1);
        }
    }

    private static int finalUnquantize(int i, boolean signed) {
        if (signed) {
            return i < 0 ? (((-i) * 31) >> 5) | 0x8000 : (i * 31) >> 5;
        } else {
            return (i * 31) >> 6;
        }
    }

    private int extendSign(int value, int bits) {
        int shift = 32 - bits;
        return value << shift >> shift;
    }

    private int transformInverse(int value, int value0, int bits, boolean signed) {
        value = (value + value0) & ((1 << bits) - 1);
        return signed ? extendSign(value, bits) : value;
    }

    private record Mode(
        boolean te,
        int pb,
        int epb,
        int rb,
        int gb,
        int bb,
        short[] ops
    ) {
    }
}
