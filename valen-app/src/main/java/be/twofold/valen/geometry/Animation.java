package be.twofold.valen.geometry;

public record Animation(
    String name,
    int frameCount,
    int frameRate,
    Vector4[][] rotations,
    Vector3[][] scales,
    Vector3[][] translations
) {
}
