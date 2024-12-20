package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.texture.*;

import java.util.function.*;

@FunctionalInterface
public interface Operation extends Function<Surface, Surface> {
    @Override
    Surface apply(Surface surface);

    default Texture map(Texture source, TextureFormat format) {
        var surfaces = source.surfaces().stream().map(this).toList();
        return source
            .withFormat(format)
            .withSurfaces(surfaces);
    }

    static RuntimeException uoe(TextureFormat format) {
        return new UnsupportedOperationException("Unsupported texture format: " + format);
    }
}
