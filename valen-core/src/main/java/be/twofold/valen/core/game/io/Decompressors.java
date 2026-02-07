package be.twofold.valen.core.game.io;

import wtf.reversed.toolbox.compress.*;

import java.nio.file.*;

public final class Decompressors {
    private static Path oodlePath;
    private static Decompressor oodleDecompressor;

    private Decompressors() {
    }

    public static Decompressor get(CompressionType type) {
        return switch (type) {
            case NONE -> Decompressor.none();
            case DEFLATE_RAW -> Decompressor.deflate(true);
            case DEFLATE_ZLIB -> Decompressor.deflate(false);
            case FAST_LZ -> Decompressor.fastLZ();
            case LZ4_BLOCK -> Decompressor.lz4Block();
            case LZ4_FRAME -> Decompressor.lz4Frame();
            case LZMA -> Decompressor.lzma();
            case OODLE -> getOodle();
        };
    }

    public static Decompressor getOodle() {
        if (oodleDecompressor == null) {
            throw new UnsupportedOperationException("Oodle decompressor not initialized");
        }
        return oodleDecompressor;
    }

    public static void setOodlePath(Path path) {
        oodleDecompressor = Decompressor.oodle(path);
    }

    public static void resetOodle() {
        oodleDecompressor = null;
    }
}
