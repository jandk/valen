package be.twofold.valen.core.compression;

import java.io.*;
import java.util.*;

abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    int getUnsignedByte(byte[] array, int offset) {
        return Byte.toUnsignedInt(array[offset]);
    }

    void copyLiteral(
        byte[] src, int srcPos, int srcLim,
        byte[] dst, int dstPos, int dstLim,
        int len
    ) throws IOException {
        if (srcPos + len > srcLim || dstPos + len > dstLim || len <= 0) {
            throw new IOException("Invalid literal");
        }
        System.arraycopy(src, srcPos, dst, dstPos, len);
    }

    void copyReference(
        byte[] dst, int dstPos, int dstLim,
        int dstOff, int offset, int length
    ) throws IOException {
        if (dstPos - offset < dstOff || dstPos + length > dstLim || offset <= 0) {
            throw new IOException("Invalid match");
        }
        if (offset == 1) {
            Arrays.fill(dst, dstPos, dstPos + length, dst[dstPos - 1]);
        } else if (offset >= length) {
            System.arraycopy(dst, dstPos - offset, dst, dstPos, length);
        } else {
            for (int i = 0, pos = dstPos - offset; i < length; i++) {
                dst[dstPos + i] = dst[pos + i];
            }
        }
    }
}
