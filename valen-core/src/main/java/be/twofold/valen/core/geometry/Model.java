package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record Model(
    List<Mesh> meshes,
    Optional<Skeleton> skeleton,
    Optional<String> name,
    Optional<Hair> hair,
    Axis upAxis
) {
    public Model {
        meshes = List.copyOf(meshes);
        Check.nonNull(upAxis, "upAxis");
    }

    public Model(List<Mesh> meshes, Axis upAxis) {
        this(meshes, Optional.empty(), Optional.empty(), Optional.empty(), upAxis);
    }

    public Model withSkeleton(Optional<Skeleton> skeleton) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }

    public Model withName(Optional<String> name) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }

    public Model withHair(Optional<Hair> hair) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }
}
