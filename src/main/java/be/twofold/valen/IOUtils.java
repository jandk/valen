package be.twofold.valen;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class IOUtils {
    private IOUtils() {
    }

    public static <T> T readStruct(ReadableByteChannel channel, int size, Function<ByteBuffer, T> reader) throws IOException {
        return reader.apply(readBuffer(channel, size));
    }

    public static <T> List<T> readStructs(ReadableByteChannel channel, int count, int size, Function<ByteBuffer, T> reader) throws IOException {
        int totalSize = count * size;
        ByteBuffer buffer = readBuffer(channel, totalSize);
        return IntStream.range(0, count)
            .mapToObj(i -> buffer.slice(i * size, size).order(ByteOrder.LITTLE_ENDIAN))
            .map(reader)
            .toList();
    }

    private static ByteBuffer readBuffer(ReadableByteChannel channel, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        int read = channel.read(buffer);
        if (read != size) {
            throw new IOException("Expected to read " + size + " bytes, but got " + read);
        }
        buffer.flip();
        return buffer;
    }

    public static long[] readLongs(ReadableByteChannel channel, int count) throws IOException {
        long[] longs = new long[count];
        readBuffer(channel, count * Long.BYTES).asLongBuffer().get(longs);
        return longs;
    }
}
