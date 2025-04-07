module valen.export.exr {
    requires valen.core;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.exr.ExrExporter;
}
