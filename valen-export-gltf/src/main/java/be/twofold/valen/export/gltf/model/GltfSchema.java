package be.twofold.valen.export.gltf.model;

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
    int scene
) {
}
