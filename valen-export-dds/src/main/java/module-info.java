module valen.export.dds {
    requires valen.core;
    requires valen.export;

    provides be.twofold.valen.export.Exporter
        with be.twofold.valen.export.dds.DdsExporter;
}
