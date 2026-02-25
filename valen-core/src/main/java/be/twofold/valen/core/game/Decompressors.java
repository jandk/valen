package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import org.slf4j.*;
import wtf.reversed.toolbox.compress.*;

import java.nio.file.*;

public final class Decompressors {
    private static final Logger log = LoggerFactory.getLogger(Decompressors.class);

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
            if (Platform.current().os() == Platform.OS.WINDOWS &&
                Platform.current().arch() == Platform.Arch.X86_64) {
                log.warn("Oodle decompressor not initialized for Windows x64");
            }
            oodleDecompressor = Decompressor.oodle(OodleDownloader.download());
        }
        return oodleDecompressor;
    }

    public static void setOodlePath(Path path) {
        if (Platform.current().os() != Platform.OS.WINDOWS ||
            Platform.current().arch() != Platform.Arch.X86_64) {
            log.warn("Can't set oodle for non Windows x64 platforms");
            return;
        }
        oodleDecompressor = Decompressor.oodle(path);
    }

    public static void resetOodle() {
        oodleDecompressor = null;
    }
}
