module valen.export.gltf {
    requires com.google.gson;
    requires valen.core;
    requires valen.export.png;
    requires valen.gltf;
    requires org.slf4j;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.gltf.GlbModelExporter, be.twofold.valen.export.gltf.GlbSceneExporter;
}
