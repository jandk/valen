module valen.core {
    requires be.twofold.tinybcdec;
    requires java.logging;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires wtf.reversed.toolbox;

    requires static java.compiler;

    exports be.twofold.valen.core.animation;
    exports be.twofold.valen.core.export;
    exports be.twofold.valen.core.game;
    exports be.twofold.valen.core.geometry;
    exports be.twofold.valen.core.material;
    exports be.twofold.valen.core.parsing;
    exports be.twofold.valen.core.scene;
    exports be.twofold.valen.core.texture;
    exports be.twofold.valen.core.util.logging;
    exports be.twofold.valen.core.util;

    uses be.twofold.valen.core.export.Exporter;
    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.core.export.RawExporter;
}
