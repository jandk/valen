module valen.export.png {
    requires be.twofold.tinybcdec;
    requires valen.core;
    requires valen.export;

    provides be.twofold.valen.export.Exporter
        with be.twofold.valen.export.png.PngExporter;
}
