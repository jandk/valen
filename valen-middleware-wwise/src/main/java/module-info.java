module valen.middleware.wwise {
    requires valen.core;
    requires java.xml;

    exports be.twofold.valen.middleware.wwise.bnk;
    exports be.twofold.valen.middleware.wwise.info;
    exports be.twofold.valen.middleware.wwise.pck;

    provides be.twofold.valen.core.audio.AudioDecoder
        with be.twofold.valen.middleware.wwise.codec.WwiseImaDecoder;
}
