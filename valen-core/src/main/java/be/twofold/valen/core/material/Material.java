package be.twofold.valen.core.material;

import java.util.*;

public record Material(
    String name,
    List<TextureReference> textures
) {
    public Material {
        textures = List.copyOf(textures);
    }
}
