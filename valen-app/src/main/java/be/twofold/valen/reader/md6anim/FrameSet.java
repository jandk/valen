package be.twofold.valen.reader.md6anim;

import be.twofold.valen.geometry.*;

public record FrameSet(
    Vector4[] firstR,
    Vector3[] firstS,
    Vector3[] firstT,
    Vector3[] firstU,
    Vector4[][] rangeR,
    Vector3[][] rangeS,
    Vector3[][] rangeT,
    Vector3[][] rangeU,
    int frameStart,
    int frameRange
) {
}
