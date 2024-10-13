module valen.export.dmf {
    requires com.google.gson;
    requires valen.export;
    requires valen.export.png;
    requires valen.core;

    provides be.twofold.valen.export.Exporter
        with org.redeye.valen.export.dmf.DmfModelExporter;
}