package be.twofold.valen.game.eternal.reader.md6anim;

import wtf.reversed.toolbox.math.*;

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

    public int frameEnd() {
        return frameStart + frameRange;
    }
}
