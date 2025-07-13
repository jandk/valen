package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;
import be.twofold.valen.core.compression.oodle.ffm.*;
import org.slf4j.*;

import java.io.*;
import java.lang.foreign.*;
import java.net.*;
import java.net.http.*;
import java.nio.*;
import java.nio.file.*;

final class OodleDecompressor implements Decompressor {
    private static final String OODLE_URL = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/win/redist/oo2core_9_win64.dll";

    private static final Logger log = LoggerFactory.getLogger(OodleDecompressor.class);

    private final Arena arena = Arena.ofAuto();
    private final OodleFFM oodleFFM;
    private final MemorySegment decodeBuffer;

    OodleDecompressor(Path path) {
        oodleFFM = new OodleFFM(path, arena);
        log.info("Loaded Oodle version {}", getVersion());

        int memorySizeNeeded = oodleFFM.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid.value(), -1);
        decodeBuffer = arena.allocate(memorySizeNeeded);
    }

    static OodleDecompressor download() {
        var uri = URI.create(OODLE_URL);
        var fileName = Path.of(uri.getPath()).getFileName();
        if (!Files.exists(fileName)) {
            try (var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
            ) {
                var request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();

                log.info("Downloading Oodle from {}", uri);
                var response = client.send(request, HttpResponse.BodyHandlers.ofFile(fileName));
                if (response.statusCode() != 200) {
                    throw new Exception("HTTP error: " + response.statusCode());
                }

                fileName = response.body();
                log.info("Downloaded Oodle to {}", fileName);
            } catch (Exception e) {
                log.error("Failed to download Oodle", e);
                throw new RuntimeException(e);
            }
        }

        return new OodleDecompressor(fileName);
    }

    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        try (var confined = Arena.ofConfined()) {
            var srcSegment = confined.allocate(src.remaining())
                .copyFrom(MemorySegment.ofBuffer(src));
            int expected = dst.remaining();
            var dstSegment = confined.allocate(expected);

            var result = (int) oodleFFM.OodleLZ_Decompress(
                srcSegment, srcSegment.byteSize(), dstSegment, dstSegment.byteSize(),
                OodleLZ_FuzzSafe.Yes.value(), OodleLZ_CheckCRC.Yes.value(), OodleLZ_Verbosity.None.value(),
                MemorySegment.NULL, 0,
                MemorySegment.NULL, MemorySegment.NULL,
                decodeBuffer, decodeBuffer.byteSize(),
                OodleLZ_Decode_ThreadPhase.All.value()
            );

            if (result != expected) {
                throw new IOException("Decompression failed, expected " + expected + ", got " + result);
            }
            MemorySegment.ofBuffer(dst)
                .copyFrom(dstSegment);
            src.position(src.limit());
            dst.position(dst.position() + result);
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
