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

    public static ByteBuffer readBuffer(ReadableByteChannel channel, int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        int read = channel.read(buffer);
        if (read != size) {
            throw new IOException("Expected to read " + size + " bytes, but only read " + read);
        }
        return buffer.flip();
    }

    public static byte[] readBytes(ReadableByteChannel channel, int size) throws IOException {
        return readBuffer(channel, size).array();
    }

    public static int[] readInts(ReadableByteChannel channel, int count) throws IOException {
        int[] ints = new int[count];
        readBuffer(channel, count * Integer.BYTES).asIntBuffer().get(ints);
        return ints;
    }

    public static long[] readLongs(ReadableByteChannel channel, int count) throws IOException {
        long[] longs = new long[count];
        readBuffer(channel, count * Long.BYTES).asLongBuffer().get(longs);
        return longs;
    }

    public static BetterBuffer readBetterBuffer(ReadableByteChannel channel, int size) throws IOException {
        return new BetterBuffer(readBuffer(channel, size));
    }

    public static <T> T readBetterStruct(ReadableByteChannel channel, int size, Function<BetterBuffer, T> reader) throws IOException {
        return reader.apply(readBetterBuffer(channel, size));
    }

    public static <T> List<T> readBetterStructs(ReadableByteChannel channel, int count, int size, Function<BetterBuffer, T> reader) throws IOException {
        BetterBuffer buffer = readBetterBuffer(channel, count * size);
        return readBetterStructs(buffer, count, reader);
    }

    public static <T> List<T> readBetterStructs(BetterBuffer buffer, int count, Function<BetterBuffer, T> reader) {
        return Stream.generate(() -> reader.apply(buffer))
            .limit(count)
            .toList();
    }
}
