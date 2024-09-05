package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

public final class LZ4Decompressor extends Decompressor {
    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        ByteBuffer dst = ByteBuffer.allocate(dstLength);

        while (true) {
            int literal = readByte(src);

            int literalLength = readLength(src, literal >>> 4);
            dst.put(dst.position(), src, src.position(), literalLength);
            src.position(src.position() + literalLength);
            dst.position(dst.position() + literalLength);

            if (src.remaining() == 0) {
                break;
            }
            int offset = readShort(src);
            if (offset == 0) {
                throw new IOException("offset is 0");
            }

            int matchLength = readLength(src, literal & 0x0F) + 4;
            dst.put(dst.position(), dst, dst.position() - offset, matchLength);
            dst.position(dst.position() + matchLength);
        }
        return dst.flip();
    }

    private static int readByte(ByteBuffer src) {
        return Byte.toUnsignedInt(src.get());
    }

    private static int readShort(ByteBuffer src) {
        return readByte(src) | readByte(src) << 8;
    }

    private static int readLength(ByteBuffer src, int length) {
        if (length != 0x0F) {
            return length;
        }

        int read;
        do {
            read = readByte(src);
            length += read;
        } while (read == 0xFF);
        return length;
    }
}