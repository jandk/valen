package be.twofold.valen.core.util;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] readBytes(ReadableByteChannel channel, int count) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(count);
        int read = channel.read(buffer);
        if (read != count) {
            throw new IOException("Expected to read " + count + " bytes, but only read " + read);
        }
        return buffer.array();
    }
}
