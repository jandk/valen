package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;

abstract class LZDecompressor implements Decompressor {
    LZDecompressor() {
    }

    int getUnsignedByte(ByteBuffer buffer) {
        return Byte.toUnsignedInt(buffer.get());
    }

    void copyLiteral(ByteBuffer src, ByteBuffer dst, int len) throws IOException {
        if (len <= 0 || src.remaining() < len || dst.remaining() < len) {
            throw new IOException("Invalid literal");
        }
        Buffers.copy(src, dst, len);
    }

    void copyReference(ByteBuffer dst, int offset, int length) throws IOException {
        if (offset <= 0 || dst.position() - offset < 0 || length > dst.remaining()) {
            throw new IOException("Invalid match");
        }
        if (offset == 1) {
            var b = dst.get(dst.position() - 1);
            for (var i = 0; i < length; i++) {
                dst.put(b);
            }
        } else if (offset >= length) {
            dst.put(dst.slice(dst.position() - offset, length));
        } else {
            for (int i = 0, pos = dst.position() - offset; i < length; i++) {
                dst.put(dst.get(pos + i));
            }
        }
    }
}
