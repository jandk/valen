package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.function.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    List<Md6AnimMap> animMaps,
    List<FrameSet> frameSets,
    List<Quaternion> constR,
    List<Vector3> constS,
    List<Vector3> constT
) {
    public static Md6Anim read(BetterBuffer buffer) {
        var header = Md6AnimHeader.read(buffer);

        var start = buffer.position();
        var data = Md6AnimData.read(buffer);
        var animMaps = readAnimMaps(buffer);

        var animMap = animMaps.getFirst();
        var constR = readFrom(buffer, start + data.constROffset(), bb -> bb.getStructs(animMap.constR().length, Md6Anim::decodeQuat));
        var constS = readFrom(buffer, start + data.constSOffset(), bb -> bb.getStructs(animMap.constS().length, Vector3::read));
        var constT = readFrom(buffer, start + data.constTOffset(), bb -> bb.getStructs(animMap.constT().length, Vector3::read));

        var frameSetTable = readFrom(buffer, start + data.frameSetTblOffset(), bb -> bb.getBytes(data.numFrames()));
        var frameSetOffsetTable = readFrom(buffer, start + data.frameSetOffsetTblOffset(), bb -> bb.getInts(data.numFrameSets() + 1));

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + frameSetOffsetTable[i] * 16;
            var frameSet = readFrom(buffer, frameSetOffset, bb -> readFrameSet(bb, frameSetOffset, animMap));
            frameSets.add(frameSet);
        }

        if (animMaps.size() != 1) {
            throw new UnsupportedOperationException("Only one anim map is supported");
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(BetterBuffer buffer) {
        var start = buffer.position();
        var numAnimMaps = buffer.getShort();
        var tableCRCs = buffer.getShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(buffer));
        }

        List<Md6AnimMap> animMaps = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            var offset = offsets.get(i);

            var constR = readFrom(buffer, start + offset.constRRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var constS = readFrom(buffer, start + offset.constSRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var constT = readFrom(buffer, start + offset.constTRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var constU = readFrom(buffer, start + offset.constURLEOffset(), bb -> decodeRLE(bb, 0xff));

            var animR = readFrom(buffer, start + offset.animRRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var animS = readFrom(buffer, start + offset.animSRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var animT = readFrom(buffer, start + offset.animTRLEOffset(), bb -> decodeRLE(bb, 0xff));
            var animU = readFrom(buffer, start + offset.animURLEOffset(), bb -> decodeRLE(bb, 0xff));

            animMaps.add(new Md6AnimMap(tableCRCs[i], constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }

    private static FrameSet readFrameSet(BetterBuffer buffer, int frameSetOffset, Md6AnimMap animMap) {
        var animFrameSet = Md6AnimFrameSet.read(buffer);

        var firstR = readFrom(buffer, frameSetOffset + animFrameSet.firstROffset(), bb -> bb.getStructs(animMap.animR().length, Md6Anim::decodeQuat));
        var firstS = readFrom(buffer, frameSetOffset + animFrameSet.firstSOffset(), bb -> bb.getStructs(animMap.animS().length, Vector3::read));
        var firstT = readFrom(buffer, frameSetOffset + animFrameSet.firstTOffset(), bb -> bb.getStructs(animMap.animT().length, Vector3::read));

        var bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        var bitsR = readFrom(buffer, frameSetOffset + animFrameSet.RBitsOffset(), bb -> new Bits(bb.getBytes(bytesPerBone * animMap.animR().length)));
        var bitsS = readFrom(buffer, frameSetOffset + animFrameSet.SBitsOffset(), bb -> new Bits(bb.getBytes(bytesPerBone * animMap.animS().length)));
        var bitsT = readFrom(buffer, frameSetOffset + animFrameSet.TBitsOffset(), bb -> new Bits(bb.getBytes(bytesPerBone * animMap.animT().length)));

        var rangeR = readFrom(buffer, frameSetOffset + animFrameSet.rangeROffset(), bb -> bb.getStructs(bitsR.cardinality(), Md6Anim::decodeQuat));
        var rangeS = readFrom(buffer, frameSetOffset + animFrameSet.rangeSOffset(), bb -> bb.getStructs(bitsS.cardinality(), Vector3::read));
        var rangeT = readFrom(buffer, frameSetOffset + animFrameSet.rangeTOffset(), bb -> bb.getStructs(bitsT.cardinality(), Vector3::read));

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }

    private static Quaternion decodeQuat(BetterBuffer buffer) {
        var x = buffer.getShort();
        var y = buffer.getShort();
        var z = buffer.getShort();

        var xBit = (x >>> 15) & 1;
        var yBit = (y >>> 15) & 1;
        var index = (yBit << 1 | xBit);

        var a = Math.fma(x & 0x7fff, MathF.SQRT_2 / 0x7fff, -MathF.SQRT1_2);
        var b = Math.fma(y & 0x7fff, MathF.SQRT_2 / 0x7fff, -MathF.SQRT1_2);
        var c = Math.fma(z & 0x7fff, MathF.SQRT_2 / 0x7fff, -MathF.SQRT1_2);
        var d = MathF.sqrt(1 - a * a - b * b - c * c);

        return switch (index) {
            case 0 -> new Quaternion(a, b, c, d);
            case 1 -> new Quaternion(b, c, d, a);
            case 2 -> new Quaternion(c, d, a, b);
            case 3 -> new Quaternion(d, a, b, c);
            default -> throw new UnsupportedOperationException();
        };
    }

    private static <T> T readFrom(BetterBuffer buffer, int position, Function<BetterBuffer, T> read) {
        buffer.position(position);
        return read.apply(buffer);
    }

    static int[] decodeRLE(BetterBuffer buffer, int numJoints) {
        var size = Byte.toUnsignedInt(buffer.getByte());
        var length = Math.min(size, numJoints);

        var result = new int[length];
        for (var o = 0; o < length; ) {
            int count = buffer.getByte();
            if ((count & 0x80) != 0) {
                count &= 0x7F;
                Arrays.fill(result, o, o + count, (byte) numJoints);
                o += count;
                continue;
            }

            int value = buffer.getByte();
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }
}
