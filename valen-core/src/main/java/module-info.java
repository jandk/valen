module valen.core {
    requires be.twofold.tinybcdec;
    requires java.logging;
    requires java.net.http;
    requires org.slf4j;
    requires transitive wtf.reversed.toolbox;

    requires static java.compiler;

    exports be.twofold.valen.core.animation;
    exports be.twofold.valen.core.audio.decoder;
    exports be.twofold.valen.core.audio.riff;
    exports be.twofold.valen.core.audio.wave;
    exports be.twofold.valen.core.audio;
    exports be.twofold.valen.core.export;
    exports be.twofold.valen.core.game;
    exports be.twofold.valen.core.geometry.read;
    exports be.twofold.valen.core.geometry;
    exports be.twofold.valen.core.material;
    exports be.twofold.valen.core.scene;
    exports be.twofold.valen.core.text;
    exports be.twofold.valen.core.texture.shader.node;
    exports be.twofold.valen.core.texture.shader.operation;
    exports be.twofold.valen.core.texture.shader;
    exports be.twofold.valen.core.texture;
    exports be.twofold.valen.core.util.logging;
    exports be.twofold.valen.core.util;

    uses be.twofold.valen.core.audio.AudioConverter;
    uses be.twofold.valen.core.audio.AudioDecoder;
    uses be.twofold.valen.core.export.Exporter;
    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.audio.AudioDecoder with
        be.twofold.valen.core.audio.decoder.PcmU8Decoder;

    provides be.twofold.valen.core.export.Exporter with
        be.twofold.valen.core.export.RawExporter,
        be.twofold.valen.core.audio.export.NativeAudioExporter,
        be.twofold.valen.core.audio.export.WaveExporter;
}
