package be.twofold.valen.core.compression;

import java.io.*;
import java.util.*;

final class FastLZDecompressor extends Decompressor {
    @Override
    public void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException {
        Objects.checkFromIndexSize(sourceOffset, sourceLength, source.length);
        Objects.checkFromIndexSize(targetOffset, targetLength, target.length);
        Level level = Level.from(source[sourceOffset]);

        int sourceLimit = sourceOffset + sourceLength;
        int targetLimit = targetOffset + targetLength;
        int sourcePosition = sourceOffset;
        int targetPosition = targetOffset;

        boolean loop = true;
        int opcode = source[sourcePosition++] & 31;
        do {
            if ((opcode & 0xE0) == 0x00) {
                int literalLength = (opcode & 0x1F) + 1;
                if (sourcePosition + literalLength > sourceLimit) {
                    throw new IOException("Input too small");
                }
                if (targetPosition + literalLength > targetLimit) {
                    throw new IOException("Output too small");
                }
                System.arraycopy(source, sourcePosition, target, targetPosition, literalLength);
                sourcePosition += literalLength;
                targetPosition += literalLength;
            } else {
                int matchLength = (opcode >> 5) + 2;
                if ((opcode & 0xE0) == 0xE0) {
                    switch (level) {
                        case One -> matchLength += readByte(source, sourcePosition++);
                        case Two -> {
                            int temp;
                            do {
                                temp = readByte(source, sourcePosition++);
                                matchLength += temp;
                            } while (temp == 255);
                        }
                    }
                }

                int offset = (opcode & 31) << 8;
                switch (level) {
                    case One -> offset += readByte(source, sourcePosition++);
                    case Two -> {
                        int temp = readByte(source, sourcePosition++);
                        offset += temp;

                        if (temp == 255 && (opcode & 31) == 31) {
                            offset += readByte(source, sourcePosition++) << 8;
                            offset += readByte(source, sourcePosition++);
                        }
                    }
                }

                int matchPosition = targetPosition - offset - 1;
                if (matchPosition < targetOffset) {
                    throw new IOException("Offset out of range");
                }

                overlappingCopy(target, matchPosition, targetPosition, matchLength);
                targetPosition += matchLength;
            }

            if (sourcePosition < sourceLimit) {
                opcode = readByte(source, sourcePosition++);
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
