package be.twofold.valen.core.texture;

import be.twofold.valen.core.texture.conversion.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Texture(
    int width,
    int height,
    TextureFormat format,
    boolean isCubeMap,
    List<Surface> surfaces,
    float scale,
    float bias
) {
    public Texture {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.notNull(format, "format");
        // Check.argument(!surfaces.isEmpty(), "surfaces must not be empty");
        surfaces = List.copyOf(surfaces);
    }

    public Texture(int width, int height, TextureFormat format, boolean isCubeMap, List<Surface> surfaces) {
        this(width, height, format, isCubeMap, surfaces, 1.0f, 0.0f);
    }

    public Texture withFormat(TextureFormat format) {
        return new Texture(width, height, format, isCubeMap, surfaces, scale, bias);
    }

    public Texture withSurfaces(List<Surface> surfaces) {
        return new Texture(width, height, format, isCubeMap, surfaces, scale, bias);
    }

    public Texture withScaleAndBias(float scale, float bias) {
        return new Texture(width, height, format, isCubeMap, surfaces, scale, bias);
    }

    public Texture firstOnly() {
        return fromSurface(surfaces.getFirst(), format, scale, bias);
    }

    public Texture convert(TextureFormat format, boolean reconstructZ) {
        return Conversion.convert(this, format, reconstructZ);
    }

    public static Texture fromSurface(Surface surface, TextureFormat format) {
        return fromSurface(surface, format, 1.0f, 0.0f);
    }

    public static Texture fromSurface(Surface surface, TextureFormat format, float scale, float bias) {
        return new Texture(
            surface.width(),
            surface.height(),
            format,
            false,
            List.of(surface),
            scale,
            bias
        );
    }
}
