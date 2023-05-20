package be.twofold.valen.writer.png;

public record Png(
    PngFormat format,
    byte[] data
) {
}
