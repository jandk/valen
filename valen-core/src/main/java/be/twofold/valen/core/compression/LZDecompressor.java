package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    void copyLiteral(Bytes src, int srcOff, MutableBytes dst, int dstOff, int len) {
        Check.fromIndexSize(srcOff, len, src.length());
        Check.fromIndexSize(dstOff, len, dst.length());

        src.slice(srcOff, srcOff + len).copyTo(dst, dstOff);
    }

    void copyReference(MutableBytes dst, int dstOff, int offset, int length) {
        Check.fromIndexSize(dstOff, length, dst.length());
        Check.argument(offset > 0 && dstOff - offset >= 0, "Invalid match");

        int srcPos = dstOff - offset;
        if (offset == 1) {
            byte b = dst.get(dstOff - 1);
            dst.slice(dstOff, dstOff + length).fill(b);
        } else if (offset >= length) {
            dst.slice(srcPos, srcPos + length).copyTo(dst, dstOff);
        } else {
            for (int i = 0; i < length; i++) {
                dst.set(dstOff + i, dst.get(srcPos + i));
            }
        }
    }
}
