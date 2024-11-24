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
        int dstOffOrig = dstOff;
        while (true) {
            int token = src[srcOff++];

            // Get the literal len
            int literalLength = (token >>> 4) & 0x0F;
            if (literalLength != 0) {
                if (literalLength == 15) {
                    int temp;
                    do {
                        temp = readByte(src, srcOff++);
                        literalLength += temp;
                    } while (temp == 255);
                }

                // Copy the literal over
                if (srcOff + literalLength > srcLim) {
                    throw new IOException("Input too small");
                }
                if (dstOff + literalLength > dstLim) {
                    throw new IOException("Output too small");
                }
                System.arraycopy(src, srcOff, dst, dstOff, literalLength);
                srcOff += literalLength;
                dstOff += literalLength;
            }

            // End of input check
            if (srcOff == srcLim) {
                return /*dstOff - targetOffset*/;
            }

            // Get the match position, can't start before the output start
            int offset = Short.toUnsignedInt(ByteArrays.getShort(src, srcOff));
            srcOff += 2;

            int matchPosition = dstOff - offset;
            if (matchPosition < dstOffOrig || offset == 0) {
                throw new IOException("Offset out of range");
            }

            // Get the match length
            int matchLength = token & 0x0F;
            if (matchLength == 15) {
                int temp;
                do {
                    temp = readByte(src, srcOff++);
                    matchLength += temp;
                } while (temp == 255);
            }
            matchLength += 4;

            // Can't copy past the end of the output
            if (dstOff + matchLength > dstLim) {
                throw new IOException("Output past end of dst");
            }
            overlappingCopy(dst, matchPosition, dstOff, matchLength);
            dstOff += matchLength;
        }
    }

    private void overlappingCopy(byte[] array, int srcOff, int dstOff, int length) {
        for (int i = 0; i < length; i++) {
            array[dstOff + i] = array[srcOff + i];
        }
    }

    private int readByte(byte[] array, int offset) {
        return Byte.toUnsignedInt(array[offset]);
    }
}
