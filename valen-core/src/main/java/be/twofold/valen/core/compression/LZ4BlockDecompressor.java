package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

final class LZ4BlockDecompressor extends LZDecompressor {
    static final LZ4BlockDecompressor INSTANCE = new LZ4BlockDecompressor();

    private LZ4BlockDecompressor() {
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        // Special case
        if (dst.length() == 0) {
            if (src.length() != 1 || src.get(0) != 0) {
                throw new IOException("Invalid empty block");
            }
            return /*0*/;
        }

        int srcOff = 0;
        int dstOff = 0;
        while (true) {
            int token = src.get(srcOff++);

            // Get the literal len
            int literalLength = (token >>> 4) & 0x0F;
            if (literalLength != 0) {
                if (literalLength == 15) {
                    int temp;
                    do {
                        temp = src.getUnsigned(srcOff++);
                        literalLength += temp;
                    } while (temp == 255);
                }

                // Copy the literal over
                copyLiteral(src, srcOff, dst, dstOff, literalLength);
                srcOff += literalLength;
                dstOff += literalLength;
            }

            // End of input check
            if (srcOff >= src.length()) {
                return /*dstPos - targetOffset*/;
            }

            // Get the match position, can't start before the output start
            int offset = src.getUnsignedShort(srcOff);
            srcOff += 2;

            // Get the match length
            int matchLength = token & 0x0F;
            if (matchLength == 15) {
                int temp;
                do {
                    temp = src.getUnsigned(srcOff++);
                    matchLength += temp;
                } while (temp == 255);
            }
            matchLength += 4;

            // Can't copy past the end of the output
            copyReference(dst, dstOff, offset, matchLength);
            dstOff += matchLength;
        }
    }
}
