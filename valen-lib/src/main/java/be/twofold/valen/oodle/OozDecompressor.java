package be.twofold.valen.oodle;

import com.sun.jna.*;

import java.util.*;

public final class OozDecompressor {
    private static final OozLibrary Ooz = Native.load("ooz-1.0.2", OozLibrary.class);

    private OozDecompressor() {
    }

    public static byte[] decompress(byte[] data, int uncompressedSize) {
        if (data.length == uncompressedSize) {
            return data;
        }

        int offset = scanOffset(data);
        if (offset > 0) {
            data = Arrays.copyOfRange(data, offset, data.length);
            System.out.println("OozDecompressor: offset = " + offset);
        }

        byte[] rawBuf = new byte[uncompressedSize];
        int result = Ooz.Kraken_Decompress(
            data, data.length, rawBuf, rawBuf.length
        );

        if (result != uncompressedSize) {
            System.err.println("OozLZ_Decompress failed: " + result);
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
