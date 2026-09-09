module valen.export.wav {
    requires valen.core;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.export.wav.WavExporter;
}
