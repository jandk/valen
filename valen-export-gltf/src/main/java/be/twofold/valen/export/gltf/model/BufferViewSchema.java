package be.twofold.valen.export.gltf.model;

public record BufferViewSchema(
    int buffer,
    int byteOffset,
    int byteLength,
    BufferViewTarget target
) {
}
