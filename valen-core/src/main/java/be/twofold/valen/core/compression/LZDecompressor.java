package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    void copyLiteral(Bytes src, int srcOff, MutableBytes dst, int dstOff, int length) {
        Check.fromIndexSize(srcOff, length, src.length());
        Check.fromIndexSize(dstOff, length, dst.length());

        src.slice(srcOff, length).copyTo(dst, dstOff);
    }

    void copyReference(MutableBytes dst, int dstOff, int offset, int length) {
        Check.fromIndexSize(dstOff, length, dst.length());
        Check.argument(offset > 0 && dstOff - offset >= 0, "Invalid match");

        int srcPos = dstOff - offset;
        if (offset == 1) {
            byte b = dst.get(dstOff - 1);
            dst.slice(dstOff, length).fill(b);
        } else if (offset >= length) {
            dst.slice(srcPos, length).copyTo(dst, dstOff);
        } else {
            dst.slice(srcPos, offset).copyTo(dst, dstOff);
            int copied = offset;
            while (copied < length) {
                int chunk = Math.min(copied, length - copied);
                dst.slice(dstOff, chunk).copyTo(dst, dstOff + copied);
                copied += chunk;
            }
        }
    }
}
