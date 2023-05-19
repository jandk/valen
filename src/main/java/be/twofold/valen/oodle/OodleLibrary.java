package be.twofold.valen.oodle;

import com.sun.jna.*;

public interface OodleLibrary extends Library {
    int ThreadPhase_1 = 1;
    int ThreadPhase_2 = 2;
    int ThreadPhase_All = 3;

    int FuzzSafe_No = 0;
    int FuzzSafe_Yes = 1;

    int CheckCRC_No = 0;
    int CheckCRC_Yes = 1;

    int Verbosity_None = 0;
    int Verbosity_Minimal = 1;
    int Verbosity_Some = 2;
    int Verbosity_Lots = 3;

    int Compressor_Invalid = -1;
    int Compressor_None = 3;
    int Compressor_Kraken = 8;
    int Compressor_Leviathan = 13;
    int Compressor_Mermaid = 9;
    int Compressor_Selkie = 11;
    int Compressor_Hydra = 12;
    int Compressor_BitKnit = 10;
    int Compressor_LZB16 = 4;
    int Compressor_LZNA = 7;
    int Compressor_LZH = 0;
    int Compressor_LZHLW = 1;
    int Compressor_LZNIB = 2;
    int Compressor_LZBLW = 5;
    int Compressor_LZA = 6;

    long OodleLZ_Decompress(
        byte[] compBuf,
        long compBufSize,
        byte[] rawBuf,
        long rawLen,
        int fuzzSafe,
        int checkCRC,
        int verbosity,
        byte[] decBufBase,
        long decBufSize,
        long fpCallback,
        long callbackUserData,
        Pointer decoderMemory,
        long decoderMemorySize,
        int threadPhase
    );

    int OodleLZDecoder_MemorySizeNeeded(
        int compressor,
        int rawLen
    );
}
