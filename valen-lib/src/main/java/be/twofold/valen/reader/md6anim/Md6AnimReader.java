package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public final class Md6AnimReader {
    private final BetterBuffer buffer;

    private int dataOffset;
    private Md6AnimData data;
    private List<AnimMap> animMaps;
    private byte[] frameSetTable;
    private int[] frameSetOffsetTable;

    private Md6AnimReader(BetterBuffer buffer) {
        this.buffer = buffer;
    }

    public static Md6Anim read(BetterBuffer buffer) {
        return new Md6AnimReader(buffer).read();
    }

    public Md6Anim read() {
        Md6AnimHeader header = Md6AnimHeader.read(buffer);

        dataOffset = buffer.position();
        data = Md6AnimData.read(buffer);

        animMaps = readAnimMaps();
        if (animMaps.size() != 1) {
            System.out.println("Found " + animMaps.size() + " anim maps, expected 1");
        }

        frameSetTable = readFrameSetTable();
        frameSetOffsetTable = readFrameSetOffsetTable();

        List<FrameSet> frameSets = new ArrayList<>();
        for (int i = 0; i < data.numFrameSets(); i++) {
            frameSets.add(readFrameSet(i));
        }

        Quaternion[] constR = readQuats(dataOffset + data.constROffset(), animMaps.get(0).constR().length);
        Vector3[] constS = readVector3s(dataOffset + data.constSOffset(), animMaps.get(0).constS().length);
        Vector3[] constT = readVector3s(dataOffset + data.constTOffset(), animMaps.get(0).constT().length);

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private FrameSet readFrameSet(int frameSetNumber) {
        AnimMap animMap = animMaps.get(0);

        int frameSetOffset = dataOffset + frameSetOffsetTable[frameSetNumber] * 16;
        buffer.position(frameSetOffset);
        Md6AnimFrameSet animFrameSet = Md6AnimFrameSet.read(buffer);

        int bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        Quaternion[] firstR = readQuats(frameSetOffset + animFrameSet.firstROffset(), animMap.animR().length);
        Vector3[] firstS = readVector3s(frameSetOffset + animFrameSet.firstSOffset(), animMap.animS().length);
        Vector3[] firstT = readVector3s(frameSetOffset + animFrameSet.firstTOffset(), animMap.animT().length);

        Quaternion[][] rangeR = readRangeQuats(
            frameSetOffset + animFrameSet.RBitsOffset(),
            frameSetOffset + animFrameSet.rangeROffset(),
            animMap.animR().length,
            animFrameSet.frameRange(),
            bytesPerBone
        );
        Vector3[][] rangeS = readRangeVector3s(
            frameSetOffset + animFrameSet.SBitsOffset(),
            frameSetOffset + animFrameSet.rangeSOffset(),
            animMap.animS().length,
            animFrameSet.frameRange(),
            bytesPerBone
        );
        Vector3[][] rangeT = readRangeVector3s(
            frameSetOffset + animFrameSet.TBitsOffset(),
            frameSetOffset + animFrameSet.rangeTOffset(),
            animMap.animT().length,
            animFrameSet.frameRange(),
            bytesPerBone
        );

        return new FrameSet(
            firstR, firstS, firstT, null,
            rangeR, rangeS, rangeT, null,
            animFrameSet.frameStart(), animFrameSet.frameRange()
        );
    }

    private Quaternion[] readQuats(int offset, int count) {
        BetterBuffer buffer = readBuffer(offset, 6 * count);

        Quaternion[] result = new Quaternion[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = decodeQuat(buffer);
        }
        return result;
    }

    private Vector3[] readVector3s(int offset, int count) {
        BetterBuffer buffer = readBuffer(offset, 12 * count);

        Vector3[] result = new Vector3[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = Vector3.read(buffer);
        }
        return result;
    }

    private Quaternion[][] readRangeQuats(int bitsOffset, int rangeOffset, int boneCount, int frameCount, int bytesPerBone) {
        byte[] bytes = readBytes(bitsOffset, bytesPerBone * boneCount);
        Bits bits = new Bits(bytes);
        BetterBuffer buffer = readBuffer(rangeOffset, 6 * bits.cardinality());

        Quaternion[][] result = new Quaternion[boneCount][frameCount];
        for (int bone = 0; bone < boneCount; bone++) {
            int boneOffset = bone * bytesPerBone * 8;
            for (int frame = 0; frame < frameCount; frame++) {
                if (bits.get(boneOffset + frame)) {
                    result[bone][frame] = decodeQuat(buffer);
                }
            }
        }
        return result;
    }

    private Vector3[][] readRangeVector3s(int bitsOffset, int rangeOffset, int boneCount, int frameCount, int bytesPerBone) {
        Bits bits = new Bits(readBytes(bitsOffset, bytesPerBone * boneCount));
        BetterBuffer buffer = readBuffer(rangeOffset, 12 * bits.cardinality());

        Vector3[][] result = new Vector3[boneCount][frameCount];
        for (int bone = 0; bone < boneCount; bone++) {
            int boneOffset = bone * bytesPerBone * 8;
            for (int frame = 0; frame < frameCount; frame++) {
                if (bits.get(boneOffset + frame)) {
                    result[bone][frame] = Vector3.read(buffer);
                }
            }
        }
        return result;
    }

    private static Quaternion decodeQuat(BetterBuffer buffer) {
        short x = buffer.getShort();
        short y = buffer.getShort();
        short z = buffer.getShort();

        int xBit = (x >>> 15) & 1;
        int yBit = (y >>> 15) & 1;
        int index = (yBit << 1 | xBit);

        float factor = (float) (Math.sqrt(2) / 0x8000);
        float sqrt22 = (float) (Math.sqrt(2) / 2);

        float a = (x & 0x7fff) * factor - sqrt22;
        float b = (y & 0x7fff) * factor - sqrt22;
        float c = (z & 0x7fff) * factor - sqrt22;
        float d = MathF.sqrt(1 - a * a - b * b - c * c);

        return switch (index) {
            case 0 -> new Quaternion(a, b, c, d);
            case 1 -> new Quaternion(b, c, d, a);
            case 2 -> new Quaternion(c, d, a, b);
            case 3 -> new Quaternion(d, a, b, c);
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    private BetterBuffer readBuffer(int offset, int size) {
        return BetterBuffer.wrap(readBytes(offset, size));
    }

    private byte[] readBytes(int offset, int size) {
        buffer.position(offset);
        return buffer.getBytes(size);
    }

    private List<AnimMap> readAnimMaps() {
        int position = buffer.position();
        int numAnimMaps = buffer.getShort();
        short[] tableCRCs = buffer.getShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (int i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(buffer));
        }

        List<AnimMap> animMaps = new ArrayList<>();
        for (int i = 0; i < numAnimMaps; i++) {
            Md6AnimMapOffsets offset = offsets.get(i);

            byte[] constR = readAnimMap(position + offset.constRRLEOffset());
            byte[] constS = readAnimMap(position + offset.constSRLEOffset());
            byte[] constT = readAnimMap(position + offset.constTRLEOffset());
            byte[] constU = readAnimMap(position + offset.constURLEOffset());

            byte[] animR = readAnimMap(position + offset.animRRLEOffset());
            byte[] animS = readAnimMap(position + offset.animSRLEOffset());
            byte[] animT = readAnimMap(position + offset.animTRLEOffset());
            byte[] animU = readAnimMap(position + offset.animURLEOffset());

            animMaps.add(new AnimMap(tableCRCs[i], constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }

    private byte[] readAnimMap(int position) {
        buffer.position(position);
        return RLEDecoder.decodeRLE(buffer, 0xff);
    }

    private byte[] readFrameSetTable() {
        buffer.position(dataOffset + data.frameSetTblOffset());
        return buffer.getBytes(data.numFrames());
    }

    private int[] readFrameSetOffsetTable() {
        buffer.position(dataOffset + data.frameSetOffsetTblOffset());
        return buffer.getInts(data.numFrameSets() + 1);
    }
}
