package be.twofold.valen.core.game.io;

public enum CompressionType {
    NONE,
    DEFLATE_RAW,
    DEFLATE_ZLIB,
    FAST_LZ,
    LZ4_BLOCK,
    LZ4_FRAME,
    LZMA,
    OODLE,
}
