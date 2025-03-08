package be.twofold.valen.core.material;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Material(
    String name,
    List<MaterialProperty> properties,
    boolean useAlpha
) {
    public Material {
        Check.notNull(name, "name");
        properties = List.copyOf(properties);
    }

    public Material(String name, List<MaterialProperty> properties) {
        this(name, properties, false);
    }
}
