package be.twofold.valen.writer.dds;

public record Dds(
    DdsInfo info,
    byte[][] mipMaps
) {
}
