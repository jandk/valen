package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.game.idtech.md6anim.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public record FrameSet(
    int frameStart,
    int frameRange,
    List<Quaternion> firstR,
    List<Vector3> firstS,
    List<Vector3> firstT,
    List<Quaternion> rangeR,
    List<Vector3> rangeS,
    List<Vector3> rangeT,
    Bytes bitsR,
    Bytes bitsS,
    Bytes bitsT
) {
    public int bytesPerBone() {
        return Math.ceilDiv(frameRange, 8);
    }

    public static FrameSet read(BinaryReader reader, long frameSetOffset, Md6AnimMap map) throws IOException {
        int rLength = map.animR().length();
        int sLength = map.animS().length();
        int tLength = map.animT().length();

        var animFrameSet = reader.position(frameSetOffset).readObject(Md6AnimFrameSet::read);
        var firstR = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstROffset())).readObjects(rLength, Md6AnimDecoder::decodeQuat);
        var firstS = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstSOffset())).readObjects(sLength, Vector3::read);
        var firstT = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstTOffset())).readObjects(tLength, Vector3::read);

        var bytesPerBone = Math.ceilDiv(animFrameSet.frameRange(), 8);
        var bitsR = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.RBitsOffset())).readBytes(bytesPerBone * rLength);
        var bitsS = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.SBitsOffset())).readBytes(bytesPerBone * sLength);
        var bitsT = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.TBitsOffset())).readBytes(bytesPerBone * tLength);

        var rangeR = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeROffset())).readObjects(cardinality(bitsR), Md6AnimDecoder::decodeQuat);
        var rangeS = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeSOffset())).readObjects(cardinality(bitsS), Vector3::read);
        var rangeT = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeTOffset())).readObjects(cardinality(bitsT), Vector3::read);

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }

    private static int cardinality(Bytes bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length(); i++) {
            count += Integer.bitCount(bytes.getUnsigned(i));
        }
        return count;
    }
}
