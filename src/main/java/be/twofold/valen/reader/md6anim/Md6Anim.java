package be.twofold.valen.reader.md6anim;

import be.twofold.valen.geometry.*;

import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    AnimMap animMap,
    List<FrameSet> frameSets,
    Vector4[] constR,
    Vector3[] constS,
    Vector3[] constT
) {
}
