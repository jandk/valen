package be.twofold.valen.converter;

import be.twofold.valen.converter.decoder.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.writer.png.*;

public final class ImageToPng {
    private final boolean normalizeNormalMap;

    public ImageToPng() {
        this(false);
    }

    public ImageToPng(boolean normalizeNormalMap) {
        this.normalizeNormalMap = normalizeNormalMap;
    }

    public Png convert(Image image) {
        int minMip = image.minMip();
        ImageMip mip = image.mips().get(minMip);
        int width = mip.mipPixelWidth();
        int height = mip.mipPixelHeight();

        BCDecoder decoder = mapDecoder(image.header().textureFormat());
        byte[] dst = decoder.decode(image.mipData()[minMip], width, height);

        PngFormat format = mapPngFormat(width, height, image.header().textureFormat());
        return new Png(format, dst);
    }

    private BCDecoder mapDecoder(ImageTextureFormat format) {
        return switch (format) {
            case FMT_BC1, FMT_BC1_SRGB -> new BC1Decoder(3, 0, 1, 2);
            case FMT_BC3 -> new BC3Decoder(4, 0, 1, 2, 3);
            case FMT_BC4 -> new BC4UDecoder(1, 0);
            case FMT_BC5 -> new BC5UDecoder(3, 0, 1, 2, normalizeNormalMap);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private PngFormat mapPngFormat(int width, int height, ImageTextureFormat format) {
        return switch (format) {
            case FMT_BC1, FMT_BC5 -> new PngFormat(width, height, PngColorType.Rgb, 8, true);
            case FMT_BC1_SRGB -> new PngFormat(width, height, PngColorType.Rgb, 8, false);
            case FMT_BC3 -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, true);
            case FMT_BC4 -> new PngFormat(width, height, PngColorType.Gray, 8, true);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
