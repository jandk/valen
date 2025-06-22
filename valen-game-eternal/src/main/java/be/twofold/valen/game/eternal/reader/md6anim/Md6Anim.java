package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
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

    public static Md6Anim read(DataSource source) throws IOException {
        var header = Md6AnimHeader.read(source);

        var start = Math.toIntExact(source.position());
        var data = Md6AnimData.read(source);
        var animMaps = readAnimMaps(source);
        if (animMaps.size() > 1) {
            log.warn("Multiple animMaps found, using the first one");
        }

        var animMap = animMaps.getFirst();
        var constR = source.position(start + data.constROffset()).readObjects(animMap.constR().length, Md6Anim::decodeQuat);
        var constS = source.position(start + data.constSOffset()).readObjects(animMap.constS().length, Vector3::read);
        var constT = source.position(start + data.constTOffset()).readObjects(animMap.constT().length, Vector3::read);

        var frameSetTable = source.position(start + data.frameSetTblOffset()).readBytes(data.numFrames());
        var frameSetOffsetTable = source.position(start + data.frameSetOffsetTblOffset()).readInts(data.numFrameSets() + 1);

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + frameSetOffsetTable[i] * 16;
            var frameSet = source.position(frameSetOffset).readObject(s -> readFrameSet(s, frameSetOffset, animMap));
            frameSets.add(frameSet);
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(DataSource source) throws IOException {
        var start = Math.toIntExact(source.position());
        var numAnimMaps = source.readShort();
        var tableCRCs = source.readShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(source));
        }

        List<Md6AnimMap> animMaps = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            var offset = offsets.get(i);

            var constR = source.position(start + offset.constRRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constS = source.position(start + offset.constSRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constT = source.position(start + offset.constTRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var constU = source.position(start + offset.constURLEOffset()).readObject(s -> decodeRLE(s, 0xff));

            var animR = source.position(start + offset.animRRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animS = source.position(start + offset.animSRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animT = source.position(start + offset.animTRLEOffset()).readObject(s -> decodeRLE(s, 0xff));
            var animU = source.position(start + offset.animURLEOffset()).readObject(s -> decodeRLE(s, 0xff));

            animMaps.add(new Md6AnimMap(tableCRCs[i], constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }

    private static FrameSet readFrameSet(DataSource source, int frameSetOffset, Md6AnimMap animMap) throws IOException {
        var animFrameSet = Md6AnimFrameSet.read(source);

        var firstR = source.position(frameSetOffset + animFrameSet.firstROffset()).readObjects(animMap.animR().length, Md6Anim::decodeQuat);
        var firstS = source.position(frameSetOffset + animFrameSet.firstSOffset()).readObjects(animMap.animS().length, Vector3::read);
        var firstT = source.position(frameSetOffset + animFrameSet.firstTOffset()).readObjects(animMap.animT().length, Vector3::read);

        var bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        var bitsR = source.position(frameSetOffset + animFrameSet.RBitsOffset()).readObject(s -> new Bits(s.readBytes(bytesPerBone * animMap.animR().length)));
        var bitsS = source.position(frameSetOffset + animFrameSet.SBitsOffset()).readObject(s -> new Bits(s.readBytes(bytesPerBone * animMap.animS().length)));
        var bitsT = source.position(frameSetOffset + animFrameSet.TBitsOffset()).readObject(s -> new Bits(s.readBytes(bytesPerBone * animMap.animT().length)));

        var rangeR = source.position(frameSetOffset + animFrameSet.rangeROffset()).readObjects(bitsR.cardinality(), Md6Anim::decodeQuat);
        var rangeS = source.position(frameSetOffset + animFrameSet.rangeSOffset()).readObjects(bitsS.cardinality(), Vector3::read);
        var rangeT = source.position(frameSetOffset + animFrameSet.rangeTOffset()).readObjects(bitsT.cardinality(), Vector3::read);

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
