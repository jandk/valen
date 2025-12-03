package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
import org.slf4j.*;

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
    private static final Logger log = LoggerFactory.getLogger(Md6Anim.class);

    public static Md6Anim read(BinaryReader reader) throws IOException {
        var header = Md6AnimHeader.read(reader);

        var start = Math.toIntExact(reader.position());
        var data = Md6AnimData.read(reader);
        var animMaps = readAnimMaps(reader);
        if (animMaps.size() > 1) {
            log.warn("Multiple animMaps found, using the first one");
        }

        var animMap = animMaps.getFirst();
        var constR = reader.position(start + data.constROffset()).readObjects(animMap.constR().length(), Md6Anim::decodeQuat);
        var constS = reader.position(start + data.constSOffset()).readObjects(animMap.constS().length(), Vector3::read);
        var constT = reader.position(start + data.constTOffset()).readObjects(animMap.constT().length(), Vector3::read);

        var frameSetTable = reader.position(start + data.frameSetTblOffset()).readBytes(data.numFrames());
        var frameSetOffsetTable = reader.position(start + data.frameSetOffsetTblOffset()).readInts(data.numFrameSets() + 1);

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + frameSetOffsetTable[i] * 16;
            var frameSet = reader.position(frameSetOffset).readObject(s -> readFrameSet(s, frameSetOffset, animMap));
            frameSets.add(frameSet);
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(BinaryReader reader) throws IOException {
        var start = Math.toIntExact(reader.position());
        var numAnimMaps = reader.readShort();
        var tableCRCs = reader.readShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(reader));
        }

        List<Md6AnimMap> animMaps = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            var offset = offsets.get(i);

            var constR = reader.position(start + offset.constRRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constS = reader.position(start + offset.constSRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constT = reader.position(start + offset.constTRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constU = reader.position(start + offset.constURLEOffset()).readObject(s -> decodeRLE(s, 0xff));

            var animR = reader.position(start + offset.animRRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animS = reader.position(start + offset.animSRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animT = reader.position(start + offset.animTRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animU = reader.position(start + offset.animURLEOffset()).readObject(s -> decodeRLE(s, 0xff));

            animMaps.add(new Md6AnimMap(tableCRCs[i], constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }

    private static FrameSet readFrameSet(BinaryReader reader, int frameSetOffset, Md6AnimMap animMap) throws IOException {
        var animFrameSet = Md6AnimFrameSet.read(reader);

        var firstR = reader.position(frameSetOffset + animFrameSet.firstROffset()).readObjects(animMap.animR().length(), Md6Anim::decodeQuat);
        var firstS = reader.position(frameSetOffset + animFrameSet.firstSOffset()).readObjects(animMap.animS().length(), Vector3::read);
        var firstT = reader.position(frameSetOffset + animFrameSet.firstTOffset()).readObjects(animMap.animT().length(), Vector3::read);

        var bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        var bitsR = reader.position(frameSetOffset + animFrameSet.RBitsOffset()).readObject(s -> new Bits(s.readBytesStruct(bytesPerBone * animMap.animR().length())));
        var bitsS = reader.position(frameSetOffset + animFrameSet.SBitsOffset()).readObject(s -> new Bits(s.readBytesStruct(bytesPerBone * animMap.animS().length())));
        var bitsT = reader.position(frameSetOffset + animFrameSet.TBitsOffset()).readObject(s -> new Bits(s.readBytesStruct(bytesPerBone * animMap.animT().length())));

        var rangeR = reader.position(frameSetOffset + animFrameSet.rangeROffset()).readObjects(bitsR.cardinality(), Md6Anim::decodeQuat);
        var rangeS = reader.position(frameSetOffset + animFrameSet.rangeSOffset()).readObjects(bitsS.cardinality(), Vector3::read);
        var rangeT = reader.position(frameSetOffset + animFrameSet.rangeTOffset()).readObjects(bitsT.cardinality(), Vector3::read);

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }

    private static Quaternion decodeQuat(BinaryReader reader) throws IOException {
        var x = reader.readShort();
        var y = reader.readShort();
        var z = reader.readShort();

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

    static Ints decodeRLE(BinaryReader reader, int numJoints) throws IOException {
        var size = Byte.toUnsignedInt(reader.readByte());
        var length = Math.min(size, numJoints);

        var result = MutableInts.allocate(length);
        for (var o = 0; o < length; ) {
            int count = reader.readByte();
            if ((count & 0x80) != 0) {
                count &= 0x7F;
                for (int i = o; i < o + count; i++) {
                    result.set(i, numJoints);
                }
                o += count;
                continue;
            }

            int value = Byte.toUnsignedInt(reader.readByte());
            for (var i = 0; i < count; i++) {
                result.set(o++, value + i);
            }
        }
        return result;
    }
}
