package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.model.extensions.NodeExtension;
import be.twofold.valen.export.gltf.model.extensions.lightspunctual.KHRLightsPunctualNodeExtension;
import com.google.gson.JsonObject;

import org.immutables.value.Value;
import org.immutables.gson.Gson;

import java.util.*;

@Value.Immutable
@Gson.TypeAdapters
public interface NodeSchema {
    Optional<String> name();

    Optional<Quaternion> rotation();

    Optional<Vector3> translation();

    Optional<Vector3> scale();

    List<Integer> children();

    Optional<Integer> mesh();

    Optional<Integer> skin();

    Optional<JsonObject> extras();

    @Gson.ExpectedSubtypes(KHRLightsPunctualNodeExtension.class)
    Map<String, NodeExtension> extensions();

    public static NodeSchema buildSkeletonNode(String name, Quaternion rotation, Vector3 translation, Vector3 scale, List<Integer> children) {
        return ImmutableNodeSchema.builder().name(name).rotation(rotation).translation(translation).scale(scale).children(children).build();
    }

    public static NodeSchema buildMeshSkin(int mesh, Integer skin) {
        return ImmutableNodeSchema.builder().mesh(mesh).skin(skin).build();
    }

}
