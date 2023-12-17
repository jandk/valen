package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.math.*;

public record FrameSet(
    Quaternion[] firstR,
    Vector3[] firstS,
    Vector3[] firstT,
    Vector3[] firstU,
    Quaternion[][] rangeR,
    Vector3[][] rangeS,
    Vector3[][] rangeT,
    Vector3[][] rangeU,
    int frameStart,
    int frameRange
) {
}
