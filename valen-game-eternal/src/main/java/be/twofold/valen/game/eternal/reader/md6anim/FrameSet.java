package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.math.*;

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

    public int frameEnd() {
        return frameStart + frameRange;
    }
}
