module valen.export.png {
    requires org.slf4j;
    requires valen.core;
    requires valen.format.png;

    exports be.twofold.valen.export.png
        to valen.export.gltf, valen.export.dmf;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.png.PngExporter;
}
