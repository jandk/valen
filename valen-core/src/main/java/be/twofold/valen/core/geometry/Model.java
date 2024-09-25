package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;

import java.util.*;

public record Model(
    String name,
    List<SubModel> subModels,
    List<Material> materials,
    Skeleton skeleton
) {
    public Model {
        subModels = List.copyOf(subModels);
        materials = List.copyOf(materials);
    }

    public Model(List<SubModel> subModels, List<Material> materials, Skeleton skeleton) {
        this("Object", subModels, materials, skeleton);
    }
}
