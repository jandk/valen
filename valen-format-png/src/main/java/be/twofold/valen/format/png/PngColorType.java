package be.twofold.valen.format.png;

public enum PngColorType {
    Gray(0, 1),
    Rgb(2, 3),
    // Palette(3, 1), // Not supported for now
    GrayAlpha(4, 2),
    RgbAlpha(6, 4);

    private final int code;
    private final int channels;

    PngColorType(int code, int channels) {
        this.code = code;
        this.channels = channels;
    }

    public int code() {
        return code;
    }

    public int channels() {
        return channels;
    }
}
