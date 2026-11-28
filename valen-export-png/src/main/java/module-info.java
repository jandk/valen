module valen.export.png {
    requires valen.core;
    requires valen.format.png;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.png.PngExporter;
}
