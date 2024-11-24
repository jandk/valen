package be.twofold.valen.core.compression;

import java.io.*;
import java.util.*;

final class FastLZDecompressor extends LZDecompressor {
    FastLZDecompressor() {
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        Objects.checkFromIndexSize(srcOff, srcLen, src.length);
        Objects.checkFromIndexSize(dstOff, dstLen, dst.length);
        Level level = Level.from(src[srcOff]);

        int srcLim = srcOff + srcLen;
        int dstLim = dstOff + dstLen;
        int srcPos = srcOff;
        int dstPos = dstOff;

        int opcode = src[srcPos++] & 0x1F; // Could have had a pretty loop, but no
        while (true) {
            if ((opcode & 0xE0) == 0x00) {
                // If the upper 3 bits are 0, we have a literal
                int literalLength = (opcode & 0x1F) + 1;
                copyLiteral(src, srcPos, srcLim, dst, dstPos, dstLim, literalLength);
                srcPos += literalLength;
                dstPos += literalLength;
            } else {
                // Otherwise we have a match of at least 2
                int matchLength = (opcode >> 5) + 2;
                if ((opcode & 0xE0) == 0xE0) {
                    // If all upper bits are set, we have a long match
                    switch (level) {
                        case One -> matchLength += getUnsignedByte(src, srcPos++);
                        case Two -> {
                            int temp;
                            do {
                                temp = getUnsignedByte(src, srcPos++);
                                matchLength += temp;
                            } while (temp == 0xFF);
                        }
                    }
                }

                // Then we handle the offset
                int offset = ((opcode & 0x1F) << 8) + 1;
                switch (level) {
                    case One -> offset += getUnsignedByte(src, srcPos++);
                    case Two -> {
                        int temp = getUnsignedByte(src, srcPos++);
                        offset += temp;

                        if (temp == 0xFF && (opcode & 0x1F) == 0x1F) {
                            offset += getUnsignedByte(src, srcPos++) << 8;
                            offset += getUnsignedByte(src, srcPos++);
                        }
                    }
                }

                copyReference(dst, dstPos, dstLim, dstOff, offset, matchLength);
                dstPos += matchLength;
            }

            if (srcPos == srcLim) {
                break;
            }
            opcode = getUnsignedByte(src, srcPos++);
        }

        // return dstPos - targetOffset;
    }

    private enum Level {
        One,
        Two;

        public static Level from(byte b) throws IOException {
            int level = b >> 5;
            return switch (level) {
                case 0 -> One;
                case 1 -> Two;
                default -> throw new IOException("Invalid level: " + level);
            };
        }
    }
}
