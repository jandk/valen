package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;
import be.twofold.valen.core.compression.oodle.ffm.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import org.slf4j.*;

import java.io.*;
import java.lang.foreign.*;
import java.net.*;
import java.nio.file.*;

final class OodleDecompressor implements Decompressor {
    private static final String OODLE_URL = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/win/redist/oo2core_9_win64.dll";

    private static final Logger log = LoggerFactory.getLogger(OodleDecompressor.class);

    private final Arena arena = Arena.ofAuto();
    private final OodleFFM oodleFFM;
    private final MemorySegment decodeBuffer;

    private OodleDecompressor(Path path) {
        oodleFFM = new OodleFFM(path, arena);
        log.info("Loaded Oodle version {}", getVersion());

        int memorySizeNeeded = oodleFFM.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid.value(), -1);
        decodeBuffer = arena.allocate(memorySizeNeeded);
    }

    static OodleDecompressor load(Path path) {
        return new OodleDecompressor(path);
    }

    static OodleDecompressor download() {
        var uri = URI.create(OODLE_URL);
        var fileName = Path.of(uri.getPath()).getFileName();
        if (!Files.exists(fileName)) {
            HttpUtils.downloadFile(uri, fileName);
        }

        return new OodleDecompressor(fileName);
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        try (var confined = Arena.ofConfined()) {
            var srcSegment = confined.allocate(src.size())
                .copyFrom(MemorySegment.ofBuffer(src.asBuffer()));
            var dstSegment = confined.allocate(dst.size());

            var result = (int) oodleFFM.OodleLZ_Decompress(
                srcSegment, srcSegment.byteSize(), dstSegment, dstSegment.byteSize(),
                OodleLZ_FuzzSafe.Yes.value(), OodleLZ_CheckCRC.Yes.value(), OodleLZ_Verbosity.None.value(),
                MemorySegment.NULL, 0,
                MemorySegment.NULL, MemorySegment.NULL,
                decodeBuffer, decodeBuffer.byteSize(),
                OodleLZ_Decode_ThreadPhase.All.value()
            );

            if (result != dst.size()) {
                throw new IOException("Decompression failed, expected " + dst.size() + ", got " + result);
            }
            MemorySegment.ofBuffer(dst.asMutableBuffer())
                .copyFrom(dstSegment);
        }
    }

    private String getVersion() {
        int version;
        try (var arena = Arena.ofConfined()) {
            var values = new OodleConfigValues(arena);
            oodleFFM.Oodle_GetConfigValues(values.segment());
            version = values.m_oodle_header_version();
        }

        var major = (version >>> 16) & 0xFF;
        var minor = (version >>> +8) & 0xFF;
        return "2." + major + "." + minor;
    }
}
