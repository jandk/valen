module valen.export.png {
    requires valen.core;
    requires org.slf4j;

    exports be.twofold.valen.export.png
        to valen.export.gltf;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.png.PngExporter;
}
