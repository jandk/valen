package be.twofold.valen.core.compression.oodle.jna;

import com.sun.jna.*;

import java.nio.*;

public interface OodleLibrary extends Library {
    void Oodle_GetConfigValues(int[] buffer);

    int OodleLZ_Decompress(
        ByteBuffer compBuf,
        long compBufSize,
        ByteBuffer rawBuf,
        long rawLen,
        OodleLZ_FuzzSafe fuzzSafe,
        OodleLZ_CheckCRC checkCRC,
        OodleLZ_Verbosity verbosity,
        Pointer decBufBase,
        long decBufSize,
        Pointer fpCallback,
        Pointer callbackUserData,
        Pointer decoderMemory,
        long decoderMemorySize,
        OodleLZ_Decode_ThreadPhase threadPhase
    );

    int OodleLZDecoder_MemorySizeNeeded(
        OodleLZ_Compressor compressor,
        int rawLen
    );
}
