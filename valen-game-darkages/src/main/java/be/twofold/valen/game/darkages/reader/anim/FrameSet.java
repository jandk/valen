package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

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
    Bits bitsR,
    Bits bitsS,
    Bits bitsT
) {
    public int bytesPerBone() {
        return (frameRange + 7) >> 3;
    }

    public static FrameSet read(DataSource source, long frameSetOffset, Md6AnimMap map) throws IOException {
        int rLength = map.animR().length & 0x7FFF;
        int sLength = map.animS().length & 0x7FFF;
        int tLength = map.animT().length & 0x7FFF;

        var animFrameSet = source.position(frameSetOffset).readObject(Md6AnimFrameSet::read);
        var firstR = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstROffset())).readObjects(rLength, Md6Anim::decodeQuat);
        var firstS = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstSOffset())).readObjects(sLength, Vector3::read);
        var firstT = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.firstTOffset())).readObjects(tLength, Vector3::read);

        var bytesPerBone = (animFrameSet.frameRange() + 7) >> 3;
        var bitsR = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.RBitsOffset())).readObject(s -> new Bits(s.readBytes(bytesPerBone * rLength)));
        var bitsS = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.SBitsOffset())).readObject(s -> new Bits(s.readBytes(bytesPerBone * sLength)));
        var bitsT = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.TBitsOffset())).readObject(s -> new Bits(s.readBytes(bytesPerBone * tLength)));

        var rangeR = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeROffset())).readObjects(bitsR.cardinality(), Md6Anim::decodeQuat);
        var rangeS = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeSOffset())).readObjects(bitsS.cardinality(), Vector3::read);
        var rangeT = source.position(frameSetOffset + Short.toUnsignedInt(animFrameSet.rangeTOffset())).readObjects(bitsT.cardinality(), Vector3::read);

        return new FrameSet(
            animFrameSet.frameStart(),
            animFrameSet.frameRange(),
            firstR, firstS, firstT,
            rangeR, rangeS, rangeT,
            bitsR, bitsS, bitsT
        );
    }
}
