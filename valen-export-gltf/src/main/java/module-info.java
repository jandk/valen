module valen.export.gltf {
    requires org.slf4j;
    requires valen.core;
    requires valen.export.png;
    requires valen.format.gltf;

    provides be.twofold.valen.core.export.Exporter with
        be.twofold.valen.export.gltf.GltfAnimationExporter.Binary,
        be.twofold.valen.export.gltf.GltfAnimationExporter.Split,
        be.twofold.valen.export.gltf.GltfMaterialExporter.Binary,
        be.twofold.valen.export.gltf.GltfMaterialExporter.Split,
        be.twofold.valen.export.gltf.GltfModelExporter.Binary,
        be.twofold.valen.export.gltf.GltfModelExporter.Split,
        be.twofold.valen.export.gltf.GltfSceneExporter.Binary,
        be.twofold.valen.export.gltf.GltfSceneExporter.Split;
}
