package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

public abstract class Conversion {
    Conversion() {
    }

    public static Texture convert(Texture source, TextureFormat targetFormat, boolean reconstructZ) {
        return convert(source, targetFormat, reconstructZ, 1.0f, 0.0f);
    }

    public static Texture convert(Texture source, TextureFormat targetFormat, boolean reconstructZ, float scale, float bias) {
        var sourceFormat = source.format();
        if (sourceFormat == targetFormat) {
            return source;
        }

        if (targetFormat.isCompressed()) {
            throw new UnsupportedOperationException("Compressing textures is not supported");
        }

        var decompress = new Decompress();
        var tonemap = new Tonemap();
        var unpack = new Unpack();
        var swizzle = new Swizzle();
        var scaleAndBias = new ScaleAndBias(scale, bias);

        var surfaces = source.surfaces().stream()
            .map(surface -> {
                surface = decompress.apply(surface, targetFormat);
                surface = tonemap.apply(surface, targetFormat);
                surface = unpack.apply(surface, targetFormat);
                surface = swizzle.apply(surface, targetFormat);
                surface = scaleAndBias.apply(surface, targetFormat);
                if (surface.format() != targetFormat) {
                    throw new UnsupportedOperationException("Could not convert texture from " + source.format() + " to " + targetFormat);
                }

                return surface;
            })
            .toList();

        return source
            .withFormat(targetFormat)
            .withSurfaces(surfaces);
    }

    abstract Surface apply(Surface surface, TextureFormat dstFormat);

    RuntimeException uoe(TextureFormat format) {
        return new UnsupportedOperationException("Unsupported texture format: " + format);
    }
}
