package be.twofold.valen.core.compression.oodle.ffm;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.nio.file.*;

public final class OodleFFM {
    private final SymbolLookup lookup;

    private final MethodHandle OodleLZDecoder_MemorySizeNeeded;
    private final MethodHandle OodleLZ_Compress;
    private final MethodHandle OodleLZ_Decompress;
    private final MethodHandle Oodle_GetConfigValues;

    public OodleFFM(Path path, Arena arena) {
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

    public int OodleLZDecoder_MemorySizeNeeded(int compressor, long rawLen) {
        try {
            return (int) OodleLZDecoder_MemorySizeNeeded.invokeExact(compressor, rawLen);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }

    public long OodleLZ_Compress(int compressor, MemorySegment rawBuf, long rawLen, MemorySegment compBuf, int level, MemorySegment pOptions, MemorySegment dictionaryBase, MemorySegment lrm, MemorySegment scratchMem, long scratchSize) {
        try {
            return (long) OodleLZ_Compress.invokeExact(compressor, rawBuf, rawLen, compBuf, level, pOptions, dictionaryBase, lrm, scratchMem, scratchSize);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }

    public long OodleLZ_Decompress(MemorySegment compBuf, long compBufSize, MemorySegment rawBuf, long rawLen, int fuzzSafe, int checkCRC, int verbosity, MemorySegment decBufBase, long decBufSize, MemorySegment fpCallback, MemorySegment callbackUserData, MemorySegment decoderMemory, long decoderMemorySize, int threadPhase) {
        try {
            return (long) OodleLZ_Decompress.invokeExact(compBuf, compBufSize, rawBuf, rawLen, fuzzSafe, checkCRC, verbosity, decBufBase, decBufSize, fpCallback, callbackUserData, decoderMemory, decoderMemorySize, threadPhase);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }

    public void Oodle_GetConfigValues(MemorySegment segment) {
        try {
            Oodle_GetConfigValues.invokeExact(segment);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }
}
