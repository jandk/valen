package be.twofold.valen.core.geometry;

import java.util.*;

public record Model(
    List<Mesh> meshes,
    Skeleton skeleton,
    String name
) {
    public Model {
        meshes = List.copyOf(meshes);
    }

    public Model(List<Mesh> meshes) {
        this(meshes, null, null);
    }

    public Optional<Skeleton> skeletonOpt() {
        return Optional.ofNullable(skeleton);
    }

    public Optional<String> nameOpt() {
        return Optional.ofNullable(name);
    }

    public Model withSkeleton(Skeleton skeleton) {
        return new Model(meshes, skeleton, name);
    }

    public Model withName(String name) {
        return new Model(meshes, skeleton, name);
    }
}
