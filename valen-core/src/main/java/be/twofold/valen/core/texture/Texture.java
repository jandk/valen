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
        Check.notNull(format, "format cannot be null");
        surfaces = List.copyOf(surfaces);
    }
}
