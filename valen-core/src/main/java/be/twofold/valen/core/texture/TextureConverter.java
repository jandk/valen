package be.twofold.valen.core.texture;

import be.twofold.valen.core.texture.convert.*;

import java.util.function.*;

public final class TextureConverter {
    private TextureConverter() {
    }

    public static Texture convert(Texture source, TextureFormat format) {
        if (source.format() == format) {
            return source;
        }

        if (format.isCompressed()) {
            throw new UnsupportedOperationException("Compressing textures is not supported");
        }

        source = new DecompressOperation().map(source, format);
        source = new ConvertOperation().map(source, format);
        source = new UnpackOperation(format).map(source, format);
        source = new SwizzleOperation(format).map(source, format);
        return source;
    }

    public static Texture map(Texture source, TextureFormat format, Function<Surface, Surface> surfaceMapper) {
        var surfaces = source.surfaces().stream()
            .map(surfaceMapper)
            .toList();

        return source
            .withFormat(format)
            .withSurfaces(surfaces);
    }
}
