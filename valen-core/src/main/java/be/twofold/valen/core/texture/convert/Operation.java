package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.texture.*;

import java.util.function.*;

@FunctionalInterface
public interface Operation extends Function<Surface, Surface> {
    Surface apply(Surface surface);

    default Texture map(Texture source, TextureFormat ignored) {
        var surfaces = source.surfaces().stream().map(this).toList();
        return new Texture(source.width(), source.height(), ignored, surfaces, source.isCubeMap());
    }

    static RuntimeException uoe(TextureFormat format) {
        return new UnsupportedOperationException("Unsupported texture format: " + format);
    }
}
