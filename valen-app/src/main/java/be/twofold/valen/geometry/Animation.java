package be.twofold.valen.geometry;

import be.twofold.valen.core.math.*;

public record Animation(
    String name,
    int frameCount,
    int frameRate,
    Quaternion[][] rotations,
    Vector3[][] scales,
    Vector3[][] translations
) {
}
