package be.twofold.valen.core.texture;

import java.util.*;

public record Texture(
    int width,
    int height,
    TextureFormat format,
    List<Surface> surfaces,
    boolean isCubeMap
) {
    public Texture {
        surfaces = List.copyOf(surfaces);
    }
}
