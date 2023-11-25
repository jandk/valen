package be.twofold.valen.writer.dds;

public record DdsInfo(
    int width,
    int height,
    int mipCount,
    DxgiFormat format,
    boolean isCubeMap
) {
}
