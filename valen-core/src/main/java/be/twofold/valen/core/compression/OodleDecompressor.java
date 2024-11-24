package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;
import be.twofold.valen.core.compression.oodle.ffm.*;

import java.io.*;
import java.lang.foreign.*;
import java.nio.file.*;

final class OodleDecompressor implements Decompressor {
    private final Arena arena = Arena.ofAuto();
    private final OodleFFM oodleFFM;
    private final MemorySegment decodeBuffer;

    OodleDecompressor(Path path) {
        oodleFFM = new OodleFFM(path, arena);
        System.out.println("Loaded oodle version " + getVersion());

        int memorySizeNeeded = oodleFFM.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid.value(), -1);
        decodeBuffer = arena.allocate(memorySizeNeeded);
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        try (var confined = Arena.ofConfined()) {
            var srcSegment = confined.allocate(srcLen)
                .copyFrom(MemorySegment.ofArray(src).asSlice(srcOff, srcLen));
            var dstSegment = confined.allocate(dstLen);

            var result = (int) oodleFFM.OodleLZ_Decompress(
                srcSegment, srcLen, dstSegment, dstLen,
                OodleLZ_FuzzSafe.Yes.value(), OodleLZ_CheckCRC.Yes.value(), OodleLZ_Verbosity.None.value(),
                MemorySegment.NULL, 0,
                MemorySegment.NULL, MemorySegment.NULL,
                decodeBuffer, decodeBuffer.byteSize(),
                OodleLZ_Decode_ThreadPhase.All.value()
            );
            if (result != dstLen) {
                throw new IOException("Decompression failed");
            }
            MemorySegment.ofArray(dst)
                .asSlice(dstOff, dstLen)
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
