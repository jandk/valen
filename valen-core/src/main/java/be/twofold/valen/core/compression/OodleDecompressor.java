package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import org.slf4j.*;

import java.io.*;
import java.lang.foreign.*;
import java.lang.invoke.*;
import java.net.*;
import java.nio.file.*;

final class OodleDecompressor implements Decompressor {
    private static final String OODLE_URL = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/win/redist/oo2core_9_win64.dll";
    private static final Logger log = LoggerFactory.getLogger(OodleDecompressor.class);

    private final FFM ffm;
    private final MemorySegment decodeBuffer;

    private OodleDecompressor(Path path) {
        ffm = new FFM(path, Arena.ofAuto());
        log.info("Loaded Oodle version {}", getVersion());

        int memorySizeNeeded = ffm.OodleLZDecoder_MemorySizeNeeded(-1 /* OodleLZ_Compressor_Invalid */, -1);
        decodeBuffer = Arena.ofAuto().allocate(memorySizeNeeded);
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
            var srcSegment = confined.allocate(src.length())
                .copyFrom(MemorySegment.ofBuffer(src.asBuffer()));
            var dstSegment = confined.allocate(dst.length());

            var result = (int) ffm.OodleLZ_Decompress(
                srcSegment, srcSegment.byteSize(), dstSegment, dstSegment.byteSize(),
                1 /* OodleLZ_FuzzSafe_Yes */,
                1 /* OodleLZ_CheckCRC_Yes */,
                0 /* OodleLZ_Verbosity_None */,
                MemorySegment.NULL, 0,
                MemorySegment.NULL, MemorySegment.NULL,
                decodeBuffer, decodeBuffer.byteSize(),
                3 /* OodleLZ_Decode_ThreadPhaseAll */
            );

            if (result != dst.length()) {
                throw new IOException("Decompression failed, expected " + dst.length() + ", got " + result);
            }
            MemorySegment.ofBuffer(dst.asMutableBuffer())
                .copyFrom(dstSegment);
        }
    }

    private String getVersion() {
        int version;
        try (var arena = Arena.ofConfined()) {
            var segment = arena.allocate(28);
            ffm.Oodle_GetConfigValues(segment);
            version = segment.get(ValueLayout.JAVA_INT, 24);
        }

        var major = (version >>> 16) & 0xFF;
        var minor = (version >>> +8) & 0xFF;
        return "2." + major + "." + minor;
    }

    private static final class FFM {
        private final SymbolLookup lookup;

        private final MethodHandle OodleLZDecoder_MemorySizeNeeded;
        private final MethodHandle OodleLZ_Compress;
        private final MethodHandle OodleLZ_Decompress;
        private final MethodHandle Oodle_GetConfigValues;

        private FFM(Path path, Arena arena) {
            lookup = SymbolLookup.libraryLookup(path, arena);

            this.OodleLZDecoder_MemorySizeNeeded = lookup("OodleLZDecoder_MemorySizeNeeded", FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT, // compressor
                ValueLayout.JAVA_LONG // rawLen
            ));
            this.OodleLZ_Compress = lookup("OodleLZ_Compress", FunctionDescriptor.of(
                ValueLayout.JAVA_LONG,
                ValueLayout.JAVA_INT,  // compressor
                ValueLayout.ADDRESS,   // rawBuf
                ValueLayout.JAVA_LONG, // rawLen
                ValueLayout.ADDRESS,   // compBuf
                ValueLayout.JAVA_INT,  // level
                ValueLayout.ADDRESS,   // pOptions
                ValueLayout.ADDRESS,   // dictionaryBase
                ValueLayout.ADDRESS,   // lrm
                ValueLayout.ADDRESS,   // scratchMem
                ValueLayout.JAVA_LONG  // scratchSize
            ));
            this.OodleLZ_Decompress = lookup("OodleLZ_Decompress", FunctionDescriptor.of(
                ValueLayout.JAVA_LONG,
                ValueLayout.ADDRESS,   // compBuf
                ValueLayout.JAVA_LONG, // compBufSize
                ValueLayout.ADDRESS,   // rawBuf
                ValueLayout.JAVA_LONG, // rawLen
                ValueLayout.JAVA_INT,  // fuzzSafe
                ValueLayout.JAVA_INT,  // checkCRC
                ValueLayout.JAVA_INT,  // verbosity
                ValueLayout.ADDRESS,   // decBufBase
                ValueLayout.JAVA_LONG, // decBufSize
                ValueLayout.ADDRESS,   // fpCallback
                ValueLayout.ADDRESS,   // callbackUserData
                ValueLayout.ADDRESS,   // decoderMemory
                ValueLayout.JAVA_LONG, // decoderMemorySize
                ValueLayout.JAVA_INT   // threadPhase
            ));
            this.Oodle_GetConfigValues = lookup("Oodle_GetConfigValues", FunctionDescriptor.ofVoid(
                ValueLayout.ADDRESS // ptr
            ));
        }

        private MethodHandle lookup(String methodName, FunctionDescriptor methodDescriptor) {
            var address = lookup.find(methodName)
                .orElseThrow(() -> new UnsatisfiedLinkError("Unresolved symbol: " + methodName));
            return Linker.nativeLinker().downcallHandle(address, methodDescriptor);
        }

        private int OodleLZDecoder_MemorySizeNeeded(int compressor, long rawLen) {
            try {
                return (int) OodleLZDecoder_MemorySizeNeeded.invokeExact(compressor, rawLen);
            } catch (Throwable e) {
                throw new AssertionError("should not reach here", e);
            }
        }

        private long OodleLZ_Compress(int compressor, MemorySegment rawBuf, long rawLen, MemorySegment compBuf, int level, MemorySegment pOptions, MemorySegment dictionaryBase, MemorySegment lrm, MemorySegment scratchMem, long scratchSize) {
            try {
                return (long) OodleLZ_Compress.invokeExact(compressor, rawBuf, rawLen, compBuf, level, pOptions, dictionaryBase, lrm, scratchMem, scratchSize);
            } catch (Throwable e) {
                throw new AssertionError("should not reach here", e);
            }
        }

        private long OodleLZ_Decompress(MemorySegment compBuf, long compBufSize, MemorySegment rawBuf, long rawLen, int fuzzSafe, int checkCRC, int verbosity, MemorySegment decBufBase, long decBufSize, MemorySegment fpCallback, MemorySegment callbackUserData, MemorySegment decoderMemory, long decoderMemorySize, int threadPhase) {
            try {
                return (long) OodleLZ_Decompress.invokeExact(compBuf, compBufSize, rawBuf, rawLen, fuzzSafe, checkCRC, verbosity, decBufBase, decBufSize, fpCallback, callbackUserData, decoderMemory, decoderMemorySize, threadPhase);
            } catch (Throwable e) {
                throw new AssertionError("should not reach here", e);
            }
        }

        private void Oodle_GetConfigValues(MemorySegment segment) {
            try {
                Oodle_GetConfigValues.invokeExact(segment);
            } catch (Throwable e) {
                throw new AssertionError("should not reach here", e);
            }
        }
    }
}
