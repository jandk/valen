package be.twofold.valen.core.material;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public record Material(
    String name,
    List<MaterialProperty> properties
) {
    public Material {
        Check.nonNull(name, "name");
        properties = List.copyOf(properties);
    }
}
