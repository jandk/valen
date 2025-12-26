module valen.export.gltf {
    requires org.slf4j;
    requires valen.core;
    requires valen.export.png;
    requires valen.format.gltf;
    requires wtf.reversed.toolbox;

    provides be.twofold.valen.core.export.Exporter with
        be.twofold.valen.export.gltf.GltfAnimationExporter,
        be.twofold.valen.export.gltf.GltfMaterialExporter,
        be.twofold.valen.export.gltf.GltfModelExporter,
        be.twofold.valen.export.gltf.GltfSceneExporter;
}
