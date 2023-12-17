package be.twofold.valen.oodle;

import com.sun.jna.*;

import java.util.*;

public final class OozDecompressor {
    private static final OozLibrary Ooz = Native.load("ooz-1.0.2", OozLibrary.class);


    public static byte[] decompress(byte[] src, int uncompressedSize) {
        if (src.length == uncompressedSize) {
            return src;
        }

        int offset = scanOffset(src);
        if (offset > 0) {
            src = Arrays.copyOfRange(src, offset, src.length);
            System.out.println("OodleDecompressor: offset = " + offset);
        }

        byte[] dst = new byte[uncompressedSize + Ooz.SafeSpace];
        int result = Ooz.Kraken_Decompress(
            src, src.length, dst, uncompressedSize
        );

        if (result != uncompressedSize) {
            System.err.println("OodleLZ_Decompress failed: " + result);
        }
        return Arrays.copyOf(dst, uncompressedSize);
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
