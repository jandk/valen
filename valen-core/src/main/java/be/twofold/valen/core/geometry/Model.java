package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;

import java.util.*;

public record Model(
    List<Mesh> meshes,
    List<Material> materials,
    Skeleton skeleton
) {
    public Model {
        meshes = List.copyOf(meshes);
        materials = List.copyOf(materials);
    }
}
