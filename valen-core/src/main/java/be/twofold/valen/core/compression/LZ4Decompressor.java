package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;

final class LZ4Decompressor extends LZDecompressor {
    LZ4Decompressor() {
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        Check.fromIndexSize(srcOff, srcLen, src.length);
        Check.fromIndexSize(dstOff, dstLen, dst.length);

        // Special case
        if (dstLen == 0) {
            if (srcLen != 1 || src[srcOff] != 0) {
                throw new IOException("Invalid empty block");
            }
            return /*0*/;
        }

        int srcLim = srcOff + srcLen;
        int dstLim = dstOff + dstLen;
        int srcPos = srcOff;
        int dstPos = dstOff;

        while (true) {
            int token = src[srcPos++];

            // Get the literal len
            int literalLength = (token >>> 4) & 0x0F;
            if (literalLength != 0) {
                if (literalLength == 15) {
                    int temp;
                    do {
                        temp = getUnsignedByte(src, srcPos++);
                        literalLength += temp;
                    } while (temp == 255);
                }

                // Copy the literal over
                copyLiteral(src, srcPos, srcLim, dst, dstPos, dstLim, literalLength);
                srcPos += literalLength;
                dstPos += literalLength;
            }

            // End of input check
            if (srcPos == srcLim) {
                return /*dstPos - targetOffset*/;
            }

            // Get the match position, can't start before the output start
            int offset = Short.toUnsignedInt(ByteArrays.getShort(src, srcPos));
            srcPos += 2;

            // Get the match length
            int matchLength = token & 0x0F;
            if (matchLength == 15) {
                int temp;
                do {
                    temp = getUnsignedByte(src, srcPos++);
                    matchLength += temp;
                } while (temp == 255);
            }
            matchLength += 4;

            // Can't copy past the end of the output
            copyReference(dst, dstPos, dstLim, dstOff, offset, matchLength);
            dstPos += matchLength;
        }
    }
}
