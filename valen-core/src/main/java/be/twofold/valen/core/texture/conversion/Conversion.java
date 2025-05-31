package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

import java.util.function.*;

public abstract class Conversion {
    Conversion() {
    }

    public static Texture convert(Texture source, TextureFormat targetFormat, boolean reconstructZ) {
        var sourceFormat = source.format();
        if (sourceFormat == targetFormat) {
            return source;
        }

        if (targetFormat.isCompressed()) {
            throw new UnsupportedOperationException("Compressing textures is not supported");
        }

        source = new Decompress(reconstructZ).apply(source, targetFormat);
        source = new Tonemap().apply(source, targetFormat);
        source = new Unpack().apply(source, targetFormat);
        source = new Swizzle().apply(source, targetFormat);
        source = new ScaleAndBias().apply(source, targetFormat);
        if (source.format() != targetFormat) {
            throw new UnsupportedOperationException("Could not convert texture from " + source.format() + " to " + targetFormat);
        }

        return source;
    }

    abstract Texture apply(Texture texture, TextureFormat dstFormat);

    Texture map(Texture source, TextureFormat format, Function<Surface, Surface> surfaceMapper) {
        var surfaces = source.surfaces().stream()
            .map(surfaceMapper)
            .toList();

        return source
            .withFormat(format)
            .withSurfaces(surfaces);
    }

    RuntimeException uoe(TextureFormat format) {
        return new UnsupportedOperationException("Unsupported texture format: " + format);
    }

    record OperatorFormat(
        UnaryOperator<Surface> operator,
        TextureFormat format
    ) {
    }
}
