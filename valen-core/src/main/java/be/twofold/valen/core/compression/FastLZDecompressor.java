package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

final class FastLZDecompressor extends LZDecompressor {
    FastLZDecompressor() {
    }

    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        Level level = Level.from(src.get(src.position()));

        int opcode = src.get() & 0x1F; // Could have had a pretty loop, but no
        while (true) {
            if ((opcode & 0xE0) == 0x00) {
                // If the upper 3 bits are 0, we have a literal
                int literalLength = (opcode & 0x1F) + 1;
                copyLiteral(src, dst, literalLength);
            } else {
                // Otherwise we have a match of at least 2
                int matchLength = (opcode >> 5) + 2;
                if ((opcode & 0xE0) == 0xE0) {
                    // If all upper bits are set, we have a long match
                    switch (level) {
                        case One -> matchLength += getUnsignedByte(src);
                        case Two -> {
                            int temp;
                            do {
                                temp = getUnsignedByte(src);
                                matchLength += temp;
                            } while (temp == 0xFF);
                        }
                    }
                }

                // Then we handle the offset
                int offset = ((opcode & 0x1F) << 8) + 1;
                switch (level) {
                    case One -> offset += getUnsignedByte(src);
                    case Two -> {
                        int temp = getUnsignedByte(src);
                        offset += temp;

                        if (temp == 0xFF && (opcode & 0x1F) == 0x1F) {
                            offset += getUnsignedByte(src) << 8 | getUnsignedByte(src);
                        }
                    }
                }

                copyReference(dst, offset, matchLength);
            }

            if (!src.hasRemaining()) {
                break;
            }
            opcode = getUnsignedByte(src);
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
