package be.twofold.valen.writer.gltf.model;

public record BufferViewSchema(
    int buffer,
    int byteOffset,
    int byteLength,
    BufferViewTarget target
) {
}
