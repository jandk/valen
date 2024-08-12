package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    List<Md6AnimMap> animMaps,
    List<FrameSet> frameSets,
    List<Quaternion> constR,
    List<Vector3> constS,
    List<Vector3> constT
) {
    public static Md6Anim read(DataSource source) throws IOException {
        var header = Md6AnimHeader.read(source);

        var start = Math.toIntExact(source.tell());
        var data = Md6AnimData.read(source);
        var animMaps = readAnimMaps(source);
        if (animMaps.size() > 1) {
            System.out.println("Multiple animMaps found, using the first one");
        }

        var animMap = animMaps.getFirst();
        var constR = readFrom(source, start + data.constROffset(), s -> s.readStructs(animMap.constR().length, Md6Anim::decodeQuat));
        var constS = readFrom(source, start + data.constSOffset(), s -> s.readStructs(animMap.constS().length, Vector3::read));
        var constT = readFrom(source, start + data.constTOffset(), s -> s.readStructs(animMap.constT().length, Vector3::read));

        var frameSetTable = readFrom(source, start + data.frameSetTblOffset(), s -> s.readBytes(data.numFrames()));
        var frameSetOffsetTable = readFrom(source, start + data.frameSetOffsetTblOffset(), s -> s.readInts(data.numFrameSets() + 1));

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + frameSetOffsetTable[i] * 16;
            var frameSet = readFrom(source, frameSetOffset, s -> readFrameSet(s, frameSetOffset, animMap));
            frameSets.add(frameSet);
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(DataSource source) throws IOException {
        var start = Math.toIntExact(source.tell());
        var numAnimMaps = source.readShort();
        var tableCRCs = source.readShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(source));
        }

        List<Md6AnimMap> animMaps = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            var offset = offsets.get(i);

            var constR = readFrom(source, start + offset.constRRLEOffset(), s -> decodeRLE(s, 0xff));
            var constS = readFrom(source, start + offset.constSRLEOffset(), s -> decodeRLE(s, 0xff));
            var constT = readFrom(source, start + offset.constTRLEOffset(), s -> decodeRLE(s, 0xff));
            var constU = readFrom(source, start + offset.constURLEOffset(), s -> decodeRLE(s, 0xff));

            var animR = readFrom(source, start + offset.animRRLEOffset(), s -> decodeRLE(s, 0xff));
            var animS = readFrom(source, start + offset.animSRLEOffset(), s -> decodeRLE(s, 0xff));
            var animT = readFrom(source, start + offset.animTRLEOffset(), s -> decodeRLE(s, 0xff));
            var animU = readFrom(source, start + offset.animURLEOffset(), s -> decodeRLE(s, 0xff));

            animMaps.add(new Md6AnimMap(tableCRCs[i], constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }

    private static FrameSet readFrameSet(DataSource source, int frameSetOffset, Md6AnimMap animMap) throws IOException {
        var animFrameSet = Md6AnimFrameSet.read(source);

        var firstR = readFrom(source, frameSetOffset + animFrameSet.firstROffset(), s -> s.readStructs(animMap.animR().length, Md6Anim::decodeQuat));
        var firstS = readFrom(source, frameSetOffset + animFrameSet.firstSOffset(), s -> s.readStructs(animMap.animS().length, Vector3::read));
        var firstT = readFrom(source, frameSetOffset + animFrameSet.firstTOffset(), s -> s.readStructs(animMap.animT().length, Vector3::read));

        var bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        var bitsR = readFrom(source, frameSetOffset + animFrameSet.RBitsOffset(), s -> new Bits(s.readBytes(bytesPerBone * animMap.animR().length)));
        var bitsS = readFrom(source, frameSetOffset + animFrameSet.SBitsOffset(), s -> new Bits(s.readBytes(bytesPerBone * animMap.animS().length)));
        var bitsT = readFrom(source, frameSetOffset + animFrameSet.TBitsOffset(), s -> new Bits(s.readBytes(bytesPerBone * animMap.animT().length)));

        var rangeR = readFrom(source, frameSetOffset + animFrameSet.rangeROffset(), s -> s.readStructs(bitsR.cardinality(), Md6Anim::decodeQuat));
        var rangeS = readFrom(source, frameSetOffset + animFrameSet.rangeSOffset(), s -> s.readStructs(bitsS.cardinality(), Vector3::read));
        var rangeT = readFrom(source, frameSetOffset + animFrameSet.rangeTOffset(), s -> s.readStructs(bitsT.cardinality(), Vector3::read));

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }

    private static Quaternion decodeQuat(DataSource source) throws IOException {
        var x = source.readShort();
        var y = source.readShort();
        var z = source.readShort();

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

    private static <T> T readFrom(DataSource source, int position, StructMapper<T> mapper) throws IOException {
        source.seek(position);
        return mapper.read(source);
    }

    static int[] decodeRLE(DataSource source, int numJoints) throws IOException {
        var size = Byte.toUnsignedInt(source.readByte());
        var length = Math.min(size, numJoints);

        var result = new int[length];
        for (var o = 0; o < length; ) {
            int count = source.readByte();
            if ((count & 0x80) != 0) {
                count &= 0x7F;
                Arrays.fill(result, o, o + count, (byte) numJoints);
                o += count;
                continue;
            }

            int value = Byte.toUnsignedInt(source.readByte());
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }
}
