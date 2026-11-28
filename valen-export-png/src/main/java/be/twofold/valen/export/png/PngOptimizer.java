package be.twofold.valen.export.png;

import be.twofold.valen.format.png.*;

final class PngOptimizer {
    private PngOptimizer() {
    }

    static PngFormat optimize(byte[] array, PngFormat format) {
        if (format.bitDepth() != 8) {
            return format;
        }

        var colorType = optimize8(array, format.colorType());
        return format.withColorType(colorType);
    }

    private static PngColorType optimize8(byte[] array, PngColorType colorType) {
        return switch (colorType) {
            case Gray -> colorType;
            case Rgb -> isGray(array, 3)
                ? toGray(array, 3)
                : PngColorType.Rgb;
            case GrayAlpha -> isOpaque(array, 2)
                ? toGray(array, 2)
                : PngColorType.GrayAlpha;
            case RgbAlpha -> {
                boolean isGray = isGray(array, 4);
                yield isOpaque(array, 4)
                    ? isGray ? toGray(array, 4) : rgbAlphaToRgb(array)
                    : isGray ? rgbAlphaToGrayAlpha(array) : PngColorType.RgbAlpha;
            }
        };
    }

    private static PngColorType toGray(byte[] array, int stride) {
        for (int i = 0, o = 0; i < array.length; i += stride, o++) {
            array[o] = array[i];
        }
        return PngColorType.Gray;
    }

    private static PngColorType rgbAlphaToRgb(byte[] array) {
        for (int i = 0, o = 0; i < array.length; i += 4, o += 3) {
            array[o/**/] = array[i/**/];
            array[o + 1] = array[i + 1];
            array[o + 2] = array[i + 2];
        }
        return PngColorType.Rgb;
    }

    private static PngColorType rgbAlphaToGrayAlpha(byte[] array) {
        for (int i = 0, o = 0; i < array.length; i += 4, o += 2) {
            array[o/**/] = array[i/**/];
            array[o + 1] = array[i + 3];
        }
        return PngColorType.GrayAlpha;
    }

    private static boolean isOpaque(byte[] data, int stride) {
        for (int i = stride - 1; i < data.length; i += stride) {
            if (data[i] != (byte) 255) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGray(byte[] data, int stride) {
        for (int i = 0; i < data.length; i += stride) {
            if (data[i] != data[i + 1] || data[i] != data[i + 2]) {
                return false;
            }
        }
        return true;
    }
}
