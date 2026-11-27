package be.twofold.valen.game.idtech.md6anim;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

/**
 * Shared MD6 animation decoders for DOOM (2016), DOOM Eternal and DOOM: The Dark Ages.
 */
public final class Md6AnimDecoder {
    public static final int MISSING = -1;
    private static final float QUAT_SCALE = FloatMath.SQRT_2 / 0x7FFF;

    private Md6AnimDecoder() {
    }

    /**
     * Decodes key frame bits. Bit {@code bone * bytesPerBone * 8 + frame} is set if the bone has a key.
     */
    public static BitSet decodeBits(BinaryReader reader, int numBones, int frameRange, boolean streamed) throws IOException {
        var bytesPerBone = Math.ceilDiv(frameRange, 8);
        var bytes = reader.readBytes(numBones * bytesPerBone).toArray();

        var result = new byte[bytes.length];
        for (var bone = 0; bone < numBones; bone++) {
            var start = bone * bytesPerBone;
            for (var i = 0; i < bytesPerBone; i++) {
                var b = bytes[streamed ? start + bytesPerBone - i - 1 : start + i];
                result[start + i] = (byte) (Integer.reverse(b) >>> 24);
            }
        }
        return BitSet.valueOf(result);
    }

    /**
     * Decodes a "smallest three" quaternion.
     */
    public static Quaternion decodeQuat(BinaryReader reader) throws IOException {
        var x = reader.readShort();
        var y = reader.readShort();
        var z = reader.readShort();

        var index = ((y >>> 15) & 1) << 1 | ((x >>> 15) & 1);

        var a = Math.fma(x & 0x7FFF, QUAT_SCALE, -FloatMath.SQRT1_2);
        var b = Math.fma(y & 0x7FFF, QUAT_SCALE, -FloatMath.SQRT1_2);
        var c = Math.fma(z & 0x7FFF, QUAT_SCALE, -FloatMath.SQRT1_2);
        var d = FloatMath.sqrt(Math.max(0.0f, 1.0f - a * a - b * b - c * c));

        return switch (index) {
            case 0 -> new Quaternion(a, b, c, d);
            case 1 -> new Quaternion(b, c, d, a);
            case 2 -> new Quaternion(c, d, a, b);
            case 3 -> new Quaternion(d, a, b, c);
            default -> throw new IllegalStateException("Unreachable");
        };
    }

    /**
     * Decodes run-length encoded bone indices. Skipped bones are {@link #MISSING}.
     *
     * @param shortHeader {@code true} for The Dark Ages
     */
    public static Ints decodeRle(BinaryReader reader, boolean shortHeader) throws IOException {
        int count;
        boolean shortIndices;
        if (shortHeader) {
            var header = reader.readUnsignedShort();
            count = header & 0x7FFF;
            shortIndices = (header & 0x8000) != 0;
        } else {
            count = reader.readUnsignedByte();
            shortIndices = false;
        }

        var result = Ints.Mutable.allocate(count);
        for (var o = 0; o < count; ) {
            var run = reader.readUnsignedByte();
            var length = Math.min(run & 0x7F, count - o);

            if ((run & 0x80) != 0) {
                for (var i = 0; i < length; i++) {
                    result.set(o++, MISSING);
                }
                continue;
            }

            var first = shortIndices ? reader.readUnsignedShort() : reader.readUnsignedByte();
            for (var i = 0; i < length; i++) {
                result.set(o++, first + i);
            }
        }
        return result;
    }
}
