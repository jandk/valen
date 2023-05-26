package be.twofold.valen;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
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

    public static BetterBuffer readBetterBuffer(ReadableByteChannel channel, int size) throws IOException {
        return new BetterBuffer(readBuffer(channel, size));
    }

    public static byte[] readBytes(ByteBuffer buffer, int size) {
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    public static byte[] readBytes(SeekableByteChannel channel, int size) throws IOException {
        return readBuffer(channel, size).array();
    }

    public static int[] readInts(ByteBuffer buffer, int count) {
        int[] ints = new int[count];
        buffer.asIntBuffer().get(ints);
        buffer.position(buffer.position() + count * Integer.BYTES);
        return ints;
    }

    public static int[] readInts(SeekableByteChannel channel, int count) throws IOException {
        return readInts(readBuffer(channel, count * Integer.BYTES), count);
    }

    public static long[] readLongs(ReadableByteChannel channel, int count) throws IOException {
        long[] longs = new long[count];
        readBuffer(channel, count * Long.BYTES).asLongBuffer().get(longs);
        return longs;
    }

    public static String readString(SeekableByteChannel channel, int size) throws IOException {
        return new String(readBytes(channel, size), StandardCharsets.US_ASCII);
    }

    public static <T> T readStruct(ByteBuffer buffer, int size, Function<ByteBuffer, T> reader) {
        ByteBuffer slice = buffer.slice().limit(size).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(buffer.position() + size);
        return reader.apply(slice);
    }

    public static <T> T readStruct(ReadableByteChannel channel, int size, Function<ByteBuffer, T> reader) throws IOException {
        return reader.apply(readBuffer(channel, size));
    }

    public static <T> List<T> readStructs(ByteBuffer buffer, int count, int size, Function<ByteBuffer, T> reader) {
        return Stream.generate(() -> readStruct(buffer, size, reader))
            .limit(count)
            .toList();
    }

    public static <T> List<T> readStructs(ReadableByteChannel channel, int count, int size, Function<ByteBuffer, T> reader) throws IOException {
        ByteBuffer buffer = readBuffer(channel, count * size);
        return readStructs(buffer, count, size, reader);
    }

    public static <T> T readBetterStruct(BetterBuffer buffer, Function<BetterBuffer, T> reader) {
        return reader.apply(buffer);
    }

    public static <T> T readBetterStruct(ReadableByteChannel channel, int size, Function<BetterBuffer, T> reader) throws IOException {
        return reader.apply(readBetterBuffer(channel, size));
    }

    public static <T> List<T> readBetterStructs(BetterBuffer buffer, int count, Function<BetterBuffer, T> reader) {
        return Stream.generate(() -> readBetterStruct(buffer, reader))
            .limit(count)
            .toList();
    }

    public static <T> List<T> readBetterStructs(ReadableByteChannel channel, int count, int size, Function<BetterBuffer, T> reader) throws IOException {
        BetterBuffer buffer = readBetterBuffer(channel, count * size);
        return readBetterStructs(buffer, count, reader);
    }
}
