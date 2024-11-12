package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;

final class LZ4Decompressor extends Decompressor {
    LZ4Decompressor() {
    }

    @Override
    public void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException {
        Check.fromIndexSize(sourceOffset, sourceLength, source.length);
        Check.fromIndexSize(targetOffset, targetLength, target.length);

        // Special case
        if (targetLength == 0) {
            if (sourceLength != 1 || source[sourceOffset] != 0) {
                throw new IOException("Invalid empty block");
            }
            return /*0*/;
        }

        int sourceLimit = sourceOffset + sourceLength;
        int targetLimit = targetOffset + targetLength;

        int sourcePosition = sourceOffset;
        int targetPosition = targetOffset;
        while (true) {
            int token = source[sourcePosition++];

            // Get the literal len
            int literalLength = (token >>> 4) & 0x0F;
            if (literalLength != 0) {
                if (literalLength == 15) {
                    int temp;
                    do {
                        temp = readByte(source, sourcePosition++);
                        literalLength += temp;
                    } while (temp == 255);
                }

                // Copy the literal over
                if (sourcePosition + literalLength > sourceLimit) {
                    throw new IOException("Input too small");
                }
                if (targetPosition + literalLength > targetLimit) {
                    throw new IOException("Output too small");
                }
                System.arraycopy(source, sourcePosition, target, targetPosition, literalLength);
                sourcePosition += literalLength;
                targetPosition += literalLength;
            }

            // End of input check
            if (sourcePosition == sourceLength) {
                return /*targetPosition - targetOffset*/;
            }

            // Get the match position, can't start before the output start
            int offset = ByteArrays.readShort(source, sourcePosition);
            int matchPosition = targetPosition - offset;
            sourcePosition += 2;
            if (matchPosition < targetOffset || offset == 0) {
                throw new IOException("Offset out of range");
            }

            // Get the match length
            int matchLength = token & 0x0F;
            if (matchLength == 15) {
                int temp;
                do {
                    temp = readByte(source, sourcePosition++);
                    matchLength += temp;
                } while (temp == 255);
            }
            matchLength += 4;

            // Can't copy past the end of the output
            if (targetPosition + matchLength > targetLimit) {
                throw new IOException("Output past end of dst");
            }
            overlappingCopy(target, matchPosition, targetPosition, matchLength);
            targetPosition += matchLength;
        }
    }

    private void overlappingCopy(byte[] array, int sourcePosition, int targetPosition, int length) {
        for (int i = 0; i < length; i++) {
            array[targetPosition + i] = array[sourcePosition + i];
        }
    }

    private int readByte(byte[] array, int offset) {
        return Byte.toUnsignedInt(array[offset]);
    }
}
