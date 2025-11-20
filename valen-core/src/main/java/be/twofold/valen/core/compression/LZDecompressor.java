package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    final Map<Integer, Integer> fills = new TreeMap<>();
    final Map<Integer, Integer> copies = new TreeMap<>();
    final Map<Integer, Integer> overlap = new TreeMap<>();
    void copyLiteral(Bytes src, int srcOff, MutableBytes dst, int dstOff, int len) {
        Check.fromIndexSize(srcOff, len, src.size());
        Check.fromIndexSize(dstOff, len, dst.size());

        src.slice(srcOff, srcOff + len).copyTo(dst, dstOff);
    }

    void copyReference(MutableBytes dst, int dstOff, int offset, int length) {
        Check.fromIndexSize(dstOff, length, dst.size());
        Check.argument(offset > 0 && dstOff - offset >= 0, "Invalid match");

        int srcPos = dstOff - offset;
        if (offset == 1) {
            byte b = dst.getByte(dstOff - 1);
            dst.slice(dstOff, dstOff + length).fill(b);
            fills.merge(length, 1, Integer::sum);
        } else if (offset >= length) {
            dst.slice(srcPos, srcPos + length).copyTo(dst, dstOff);
            copies.merge(length, 1, Integer::sum);
        } else {
            for (int i = 0; i < length; i++) {
                dst.setByte(dstOff + i, dst.getByte(srcPos + i));
            }
            overlap.merge(length, 1, Integer::sum);
        }
    }
}
