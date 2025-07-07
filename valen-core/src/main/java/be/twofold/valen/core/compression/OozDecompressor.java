package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.ooz.*;
import org.slf4j.*;

import java.io.*;
import java.lang.foreign.*;
import java.nio.*;
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
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        try (var confined = Arena.ofConfined()) {
            var srcSegment = confined.allocate(src.remaining())
                .copyFrom(MemorySegment.ofBuffer(src));
            var expected = dst.remaining();
            var dstSegment = confined.allocate(expected + 64);

            var result = oozFFM.Kraken_Decompress(
                srcSegment, srcSegment.byteSize(), dstSegment, expected
            );

            if (result != expected) {
                throw new IOException("Decompression failed, expected " + expected + ", got " + result);
            }
            MemorySegment.ofBuffer(dst)
                .copyFrom(dstSegment.asSlice(0, expected));
            src.position(src.limit());
            dst.position(dst.position() + result);
        }
    }
}
