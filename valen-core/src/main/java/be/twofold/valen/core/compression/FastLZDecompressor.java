package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

final class FastLZDecompressor extends LZDecompressor {
    FastLZDecompressor() {
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        Level level = Level.from(src.getByte(0));

        int srcOff = 0;
        int dstOff = 0;
        int opcode = src.getByte(srcOff++) & 0x1F; // Could have had a pretty loop, but no
        while (true) {
            if ((opcode & 0xE0) == 0x00) {
                // If the upper 3 bits are 0, we have a literal
                int literalLength = (opcode & 0x1F) + 1;
                copyLiteral(src, srcOff, dst, dstOff, literalLength);
                srcOff += literalLength;
                dstOff += literalLength;
            } else {
                // Otherwise we have a match of at least 2
                int matchLength = (opcode >> 5) + 2;
                if ((opcode & 0xE0) == 0xE0) {
                    // If all upper bits are set, we have a long match
                    switch (level) {
                        case One -> matchLength += src.getUnsignedByte(srcOff++);
                        case Two -> {
                            int temp;
                            do {
                                temp = src.getUnsignedByte(srcOff++);
                                matchLength += temp;
                            } while (temp == 0xFF);
                        }
                    }
                }

                // Then we handle the offset
                int offset = ((opcode & 0x1F) << 8) + 1;
                switch (level) {
                    case One -> offset += src.getUnsignedByte(srcOff++);
                    case Two -> {
                        int temp = src.getUnsignedByte(srcOff++);
                        offset += temp;

                        if (temp == 0xFF && (opcode & 0x1F) == 0x1F) {
                            offset += src.getUnsignedByte(srcOff++) << 8;
                            offset += src.getUnsignedByte(srcOff++);
                        }
                    }
                }

                copyReference(dst, dstOff, offset, matchLength);
                dstOff += matchLength;
            }

            if (srcOff >= src.size()) {
                break;
            }
            opcode = src.getUnsignedByte(srcOff++);
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
