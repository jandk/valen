package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

final class LZ4Decompressor extends LZDecompressor {
    LZ4Decompressor() {
    }

    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        // Special case
        if (dst.remaining() == 0) {
            if (src.remaining() != 1 || src.get() != 0) {
                throw new IOException("Invalid empty block");
            }
            return /*0*/;
        }

        src.order(ByteOrder.LITTLE_ENDIAN);
        while (true) {
            int token = src.get();

            // Get the literal len
            int literalLength = (token >>> 4) & 0x0F;
            if (literalLength != 0) {
                if (literalLength == 15) {
                    int temp;
                    do {
                        temp = getUnsignedByte(src);
                        literalLength += temp;
                    } while (temp == 255);
                }

                // Copy the literal over
                copyLiteral(src, dst, literalLength);
            }

            // End of input check
            if (!src.hasRemaining()) {
                return /*dstPos - targetOffset*/;
            }

            // Get the match position, can't start before the output start
            int offset = Short.toUnsignedInt(src.getShort());

            // Get the match length
            int matchLength = token & 0x0F;
            if (matchLength == 15) {
                int temp;
                do {
                    temp = getUnsignedByte(src);
                    matchLength += temp;
                } while (temp == 255);
            }
            matchLength += 4;

            // Can't copy past the end of the output
            copyReference(dst, offset, matchLength);
        }
    }
}
