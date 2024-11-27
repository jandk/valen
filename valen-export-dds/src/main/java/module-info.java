module valen.export.dds {
    requires valen.core;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.dds.DdsExporter;
}
