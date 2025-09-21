package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    void copyLiteral(Bytes src, int srcOff, MutableBytes dst, int dstOff, int len) {
        Check.fromIndexSize(srcOff, len, src.size());
        Check.fromIndexSize(dstOff, len, dst.size());

        src.subList(srcOff, srcOff + len).copyTo(dst, dstOff);
    }

    void copyReference(MutableBytes dst, int dstOff, int offset, int length) {
        Check.fromIndexSize(dstOff, length, dst.size());
        Check.argument(offset > 0 && dstOff - offset >= 0, "Invalid match");

        int dstPos = dstOff - offset;
        if (offset == 1) {
            byte b = dst.getByte(dstOff - 1);
            for (int i = 0; i < length; i++) {
                dst.setByte(dstOff + i, b);
            }
        } else if (offset >= length) {
            dst.subList(dstPos, dstPos + length).copyTo(dst, dstOff);
        } else {
            for (int i = 0; i < length; i++) {
                dst.setByte(dstOff + i, dst.getByte(dstPos + i));
            }
        }
    }
}
