module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;
    requires valen.export.png;
    requires valen.export;
    requires valen.gltf;

    provides be.twofold.valen.export.Exporter
        with be.twofold.valen.export.gltf.GlbModelExporter, be.twofold.valen.export.gltf.GlbSceneExporter;
}
