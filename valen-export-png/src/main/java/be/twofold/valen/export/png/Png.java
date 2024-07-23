package be.twofold.valen.export.png;

public record Png(
    PngFormat format,
    byte[] data
) {
}
