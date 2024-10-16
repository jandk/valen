package be.twofold.valen.core.geometry;

import java.util.*;

public record Model(
    List<Mesh> meshes,
    Skeleton skeleton
) {
    public Model {
        meshes = List.copyOf(meshes);
    }
}
