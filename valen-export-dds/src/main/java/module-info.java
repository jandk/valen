module valen.export.dds {
    requires valen.core;
    requires wtf.reversed.toolbox;

    exports be.twofold.valen.export.dds;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.dds.DdsExporter;
}
