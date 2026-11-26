package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.game.idtech.md6anim.*;
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
    BitSet bitsR,
    BitSet bitsS,
    BitSet bitsT
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

        var bitsR = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.RBitsOffset())).readObject(s -> Md6AnimDecoder.decodeBits(s, rLength, animFrameSet.frameRange()));
        var bitsS = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.SBitsOffset())).readObject(s -> Md6AnimDecoder.decodeBits(s, sLength, animFrameSet.frameRange()));
        var bitsT = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.TBitsOffset())).readObject(s -> Md6AnimDecoder.decodeBits(s, tLength, animFrameSet.frameRange()));

        var rangeR = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeROffset())).readObjects(bitsR.cardinality(), Md6AnimDecoder::decodeQuat);
        var rangeS = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeSOffset())).readObjects(bitsS.cardinality(), Vector3::read);
        var rangeT = reader.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeTOffset())).readObjects(bitsT.cardinality(), Vector3::read);

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }
}
