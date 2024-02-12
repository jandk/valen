package be.twofold.valen.export.gltf.model;

import com.google.gson.JsonObject;

import java.util.*;

public record GltfSchema(
    AssetSchema asset,
    List<AccessorSchema> accessors,
    List<BufferViewSchema> bufferViews,
    List<BufferSchema> buffers,
    List<MeshSchema> meshes,
    List<NodeSchema> nodes,
    List<SceneSchema> scenes,
    List<SkinSchema> skins,
    List<AnimationSchema> animations,
    List<String> extensionsUsed,
    List<String> extensionsRequired,

    JsonObject extensions,
    int scene
) {
}
