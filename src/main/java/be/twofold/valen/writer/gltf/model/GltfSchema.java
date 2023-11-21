package be.twofold.valen.writer.gltf.model;

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
    int scene
) {
}
