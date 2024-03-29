package be.twofold.valen.core.util;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.function.*;

public final class IOUtils {
    private IOUtils() {
    }

    public static void expectPosition(SeekableByteChannel channel, long position) throws IOException {
        if (channel.position() != position) {
            throw new IOException("Expected position " + position + ", but was " + channel.position());
        }
    }

    public static BetterBuffer readBuffer(ReadableByteChannel channel, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        int read = channel.read(buffer);
        if (read != size) {
            throw new IOException("Expected to read " + size + " bytes, but only read " + read);
        }
        return new BetterBuffer(buffer.flip());
    }

    public static byte[] readBytes(ReadableByteChannel channel, int count) throws IOException {
        return readBuffer(channel, count).getBytes(count);
    }

    public static int[] readInts(ReadableByteChannel channel, int count) throws IOException {
        return readBuffer(channel, count * Integer.BYTES).getInts(count);
    }

    public static long[] readLongs(ReadableByteChannel channel, int count) throws IOException {
        return readBuffer(channel, count * Long.BYTES).getLongs(count);
    }

    public static int[] readLongsAsInts(ReadableByteChannel channel, int count) throws IOException {
        return Arrays.stream(readLongs(channel, count))
            .mapToInt(Math::toIntExact)
            .toArray();
    }

    public static <T> T readStruct(ReadableByteChannel channel, int size, Function<BetterBuffer, T> reader) throws IOException {
        return reader.apply(readBuffer(channel, size));
    }

    public static <T> List<T> readStructs(ReadableByteChannel channel, int count, int size, Function<BetterBuffer, T> reader) throws IOException {
        return readBuffer(channel, count * size)
            .getStructs(count, reader);
    }
}
