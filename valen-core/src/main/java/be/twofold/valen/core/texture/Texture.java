package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Texture(
    int width,
    int height,
    TextureFormat format,
    List<Surface> surfaces,
    boolean isCubeMap
) {
    public Texture {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.notNull(format, "format");
        Check.argument(!surfaces.isEmpty(), "surfaces must not be empty");
        surfaces = List.copyOf(surfaces);
    }

    public Texture firstOnly() {
        return fromSurface(surfaces.getFirst());
    }

    public static Texture fromSurface(Surface surface) {
        return new Texture(
            surface.width(),
            surface.height(),
            surface.format(),
            List.of(surface),
            false
        );
    }
}
