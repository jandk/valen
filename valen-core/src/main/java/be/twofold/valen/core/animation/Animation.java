package be.twofold.valen.core.animation;

import be.twofold.valen.core.math.*;

public record Animation(
    String name,
    int frameRate,
    int frameCount,
    Quaternion[][] rotations,
    Vector3[][] scales,
    Vector3[][] translations
) {
}
