module valen.middleware.wwise {
    requires valen.core;
    requires java.xml;
    requires org.slf4j;

    exports be.twofold.valen.middleware.wwise.bnk;
    exports be.twofold.valen.middleware.wwise.info;
    exports be.twofold.valen.middleware.wwise.pck;
    exports be.twofold.valen.middleware.wwise.shared;
    exports be.twofold.valen.middleware.wwise.wem;

    provides be.twofold.valen.core.audio.AudioConverter with
        be.twofold.valen.middleware.wwise.converter.WwiseOpusConverter,
        be.twofold.valen.middleware.wwise.converter.WwiseVorbisConverter;

    provides be.twofold.valen.core.audio.AudioDecoder with
        be.twofold.valen.middleware.wwise.decoder.WwiseImaDecoder,
        be.twofold.valen.middleware.wwise.decoder.WwisePtAdpcmDecoder;
}
