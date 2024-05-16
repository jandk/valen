package be.twofold.valen.core.util;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] read(SeekableByteChannel channel, long offset, int length) throws IOException {
        channel.position(offset);
        var buffer = ByteBuffer.allocate(length);
        if (channel.read(buffer) != length) {
            throw new IOException("Failed to read " + length + " bytes");
        }
        return buffer.array();
    }
}
