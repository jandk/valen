package be.twofold.valen.writer.png;

import java.util.*;

public record PngFormat(
    int width,
    int height,
    PngColorType colorType,
    int bitDepth,
    boolean linear
) {
    public PngFormat(int width, int height, PngColorType colorType) {
        this(width, height, colorType, 8);
    }

    public PngFormat(int width, int height, PngColorType colorType, int bitDepth) {
        this(width, height, colorType, bitDepth, false);
    }

    public PngFormat {
        Objects.requireNonNull(colorType, "colorType is null");
        if (width <= 0) {
            throw new IllegalArgumentException("width must be greater than 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be greater than 0");
        }
        if (bitDepth != 8 && bitDepth != 16) {
            throw new IllegalArgumentException("bitDepth must be 8 or 16");
        }
    }

    public int bytesPerPixel() {
        return colorType.channels() * (bitDepth == 8 ? 1 : 2);
    }

    public int bytesPerRow() {
        return bytesPerPixel() * width;
    }

    public int bytesPerImage() {
        return bytesPerRow() * height;
    }
}
