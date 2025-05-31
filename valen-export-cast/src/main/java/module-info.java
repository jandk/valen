module valen.export.cast {
    requires org.slf4j;
    requires valen.core;
    requires valen.format.cast;

    provides be.twofold.valen.core.export.Exporter with
        be.twofold.valen.export.cast.CastAnimationExporter,
        be.twofold.valen.export.cast.CastMaterialExporter,
        be.twofold.valen.export.cast.CastModelExporter,
        be.twofold.valen.export.cast.CastSceneExporter;
}
