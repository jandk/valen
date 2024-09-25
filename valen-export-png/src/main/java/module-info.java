module valen.export.png {
    requires be.twofold.tinybcdec;
    requires valen.core;
    requires valen.export;

    exports be.twofold.valen.export.png
        to valen.export.gltf;

    provides be.twofold.valen.export.Exporter
        with be.twofold.valen.export.png.PngExporter;
}
