module valen.export.cast {
    requires be.twofold.tinycast;
    requires org.slf4j;
    requires valen.core;

    provides be.twofold.valen.core.export.Exporter with
        be.twofold.valen.export.cast.CastAnimationExporter,
        be.twofold.valen.export.cast.CastMaterialExporter,
        be.twofold.valen.export.cast.CastModelExporter,
        be.twofold.valen.export.cast.CastSceneExporter;
}
