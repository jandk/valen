package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import org.slf4j.*;
import wtf.reversed.toolbox.compress.*;

import java.nio.file.*;

public final class Decompressors {
    private static final Logger log = LoggerFactory.getLogger(Decompressors.class);

    private final Path oodlePath;
    private Decompressor oodle;

    public Decompressors(Path oodlePath) {
        this.oodlePath = oodlePath;
    }

    public Decompressor get(CompressionType type) {
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

    private Decompressor getOodle() {
        if (oodle == null) {
            oodle = Decompressor.oodle(resolveOodlePath());
        }
        return oodle;
    }

    private Path resolveOodlePath() {
        if (oodlePath != null) {
            if (Platform.current().os() == Platform.OS.WINDOWS &&
                Platform.current().arch() == Platform.Arch.X86_64) {
                return oodlePath;
            }
            log.warn("Ignoring Oodle path {} on non-Windows-x64 platform, downloading instead", oodlePath);
        }
        return OodleDownloader.download();
    }
}
