package be.twofold.valen.oodle;

import com.sun.jna.*;

import java.util.*;

public final class OodleDecompressor {
    private static final OodleLibrary Oodle = Native.load("oo2core_8_win64", OodleLibrary.class);
    private static final Memory decoderMemory;

    static {
        int memorySizeNeeded = Oodle.OodleLZDecoder_MemorySizeNeeded(OodleLibrary.Compressor_Invalid, -1);
        decoderMemory = new Memory(memorySizeNeeded);
    }

    private OodleDecompressor() {
    }

    public static byte[] decompress(byte[] data, int uncompressedSize) {
        if (data.length == uncompressedSize) {
            return data;
        }

        int offset = scanOffset(data);
        if (offset > 0) {
            data = Arrays.copyOfRange(data, offset, data.length);
            System.out.println("OodleDecompressor: offset = " + offset);
        }

        byte[] rawBuf = new byte[uncompressedSize];
        long result = Oodle.OodleLZ_Decompress(
            data, data.length, rawBuf, rawBuf.length,
            OodleLibrary.FuzzSafe_Yes, OodleLibrary.CheckCRC_No, OodleLibrary.Verbosity_None,
            null, 0, 0, 0, decoderMemory, decoderMemory.size(), OodleLibrary.ThreadPhase_All
        );

        if (result != (long) uncompressedSize) {
            System.err.println("OodleLZ_Decompress failed: " + result);
        }
        return rawBuf;
    }

    private static int scanOffset(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (byte) 0x8c || data[i] == (byte) 0xcc) {
                return i;
            }
        }
        throw new IllegalStateException("What kind of stream is this?");
    }

}
