module valen.core {
    requires be.twofold.tinybcdec;
    requires java.logging;
    requires java.net.http;
    requires org.slf4j;

    requires static java.compiler;

    exports be.twofold.valen.core.animation;
    exports be.twofold.valen.core.compression;
    exports be.twofold.valen.core.export;
    exports be.twofold.valen.core.game;
    exports be.twofold.valen.core.geometry;
    exports be.twofold.valen.core.hashing;
    exports be.twofold.valen.core.io;
    exports be.twofold.valen.core.material;
    exports be.twofold.valen.core.math;
    exports be.twofold.valen.core.scene;
    exports be.twofold.valen.core.texture;
    exports be.twofold.valen.core.util.collect;
    exports be.twofold.valen.core.util.logging;
    exports be.twofold.valen.core.util;

    uses be.twofold.valen.core.export.Exporter;
    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.export.Exporter
        with be.twofold.valen.core.export.RawExporter;
}
