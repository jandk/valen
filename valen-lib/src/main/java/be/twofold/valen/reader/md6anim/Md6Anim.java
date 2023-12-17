package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.math.*;

import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    List<AnimMap> animMaps,
    List<FrameSet> frameSets,
    Quaternion[] constR,
    Vector3[] constS,
    Vector3[] constT
) {
}
