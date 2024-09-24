package be.twofold.valen.core.material;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Material(
    String name,
    List<TextureReference> textures
) {
    public Material {
        Check.notNull(name, "name");
        textures = List.copyOf(textures);
    }
}
