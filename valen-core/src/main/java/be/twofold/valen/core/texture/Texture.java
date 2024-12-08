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
        surfaces = List.copyOf(surfaces);
    }
}
