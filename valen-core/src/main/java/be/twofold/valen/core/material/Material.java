package be.twofold.valen.core.material;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Material(
    String name,
    List<MaterialProperty> properties
) {
    public Material {
        Check.notNull(name, "name");
        properties = List.copyOf(properties);
    }
}
