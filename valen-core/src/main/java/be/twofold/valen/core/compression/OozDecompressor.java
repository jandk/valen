package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.ooz.*;
import be.twofold.valen.core.util.collect.*;
import org.slf4j.*;

import java.io.*;
import java.lang.foreign.*;
import java.nio.file.*;

final class OozDecompressor implements Decompressor {
    private static final Logger log = LoggerFactory.getLogger(OozDecompressor.class);

    private final Arena arena = Arena.ofAuto();
    private final OozFFM oozFFM;

    OozDecompressor(Path path) {
        oozFFM = new OozFFM(path, arena);
        log.info("Loaded OOZ");
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        try (var confined = Arena.ofConfined()) {
            var srcSegment = confined.allocate(src.length())
                .copyFrom(MemorySegment.ofBuffer(src.asBuffer()));
            var dstSegment = confined.allocate(dst.length() + 64);

            var result = oozFFM.Kraken_Decompress(
                srcSegment, srcSegment.byteSize(), dstSegment, dst.length()
            );

            if (result != dst.length()) {
                throw new IOException("Decompression failed, expected " + dst.length() + ", got " + result);
            }
            MemorySegment.ofBuffer(dst.asMutableBuffer())
                .copyFrom(dstSegment.asSlice(0, dst.length()));
        }
    }
}
