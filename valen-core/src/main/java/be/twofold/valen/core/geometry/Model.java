package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Model(
    List<Mesh> meshes,
    Optional<Skeleton> skeleton,
    Optional<String> name,
    Optional<Hair> hair,
    Axis upAxis
) {
    public Model {
        meshes = List.copyOf(meshes);
        Check.notNull(upAxis, "upAxis");
    }

    public Model(List<Mesh> meshes, Axis upAxis) {
        this(meshes, Optional.empty(), Optional.empty(), Optional.empty(), upAxis);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Model withSkeleton(Optional<Skeleton> skeleton) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Model withName(Optional<String> name) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Model withHair(Optional<Hair> hair) {
        return new Model(meshes, skeleton, name, hair, upAxis);
    }
}
