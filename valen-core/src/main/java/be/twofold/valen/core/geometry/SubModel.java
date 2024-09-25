package be.twofold.valen.core.geometry;

import java.util.*;

public record SubModel(
    String name,
    List<Mesh> meshes
) {
    public SubModel {
        meshes = List.copyOf(meshes);
    }

    public SubModel(List<Mesh> meshes) {
        this("Object", meshes);
    }
}
