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

        int sourceLimit = srcOff + srcLen;
        int targetLimit = dstOff + dstLen;
        int dstOffOrig = dstOff;

        boolean loop = true;
        int opcode = src[srcOff++] & 31;
        do {
            if ((opcode & 0xE0) == 0x00) {
                int literalLength = (opcode & 0x1F) + 1;
                if (srcOff + literalLength > sourceLimit) {
                    throw new IOException("Input too small");
                }
                if (dstOff + literalLength > targetLimit) {
                    throw new IOException("Output too small");
                }
                System.arraycopy(src, srcOff, dst, dstOff, literalLength);
                srcOff += literalLength;
                dstOff += literalLength;
            } else {
                int matchLength = (opcode >> 5) + 2;
                if ((opcode & 0xE0) == 0xE0) {
                    switch (level) {
                        case One -> matchLength += readByte(src, srcOff++);
                        case Two -> {
                            int temp;
                            do {
                                temp = readByte(src, srcOff++);
                                matchLength += temp;
                            } while (temp == 255);
                        }
                    }
                }

                int offset = (opcode & 31) << 8;
                switch (level) {
                    case One -> offset += readByte(src, srcOff++);
                    case Two -> {
                        int temp = readByte(src, srcOff++);
                        offset += temp;

                        if (temp == 255 && (opcode & 31) == 31) {
                            offset += readByte(src, srcOff++) << 8;
                            offset += readByte(src, srcOff++);
                        }
                    }
                }

                int matchPosition = dstOff - offset - 1;
                if (matchPosition < dstOffOrig) {
                    throw new IOException("Offset out of range");
                }

                overlappingCopy(dst, matchPosition, dstOff, matchLength);
                dstOff += matchLength;
            }

            if (srcOff < sourceLimit) {
                opcode = readByte(src, srcOff++);
            } else {
                loop = false;
            }
        } while (loop);

        // return dstPos - targetOffset;
    }

    private static void overlappingCopy(byte[] array, int srcPos, int dstPos, int len) {
        for (int i = 0; i < len; i++) {
            array[dstPos + i] = array[srcPos + i];
        }
    }

    private int readByte(byte[] array, int offset) {
        return Byte.toUnsignedInt(array[offset]);
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
