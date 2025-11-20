module valen.export.dds {
    requires valen.core;

    exports be.twofold.valen.export.dds;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.dds.DdsExporter;
}
