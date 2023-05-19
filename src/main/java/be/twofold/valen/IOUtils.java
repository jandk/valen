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

    public static int[] readInts(SeekableByteChannel channel, int count) throws IOException {
        int[] ints = new int[count];
        readBuffer(channel, count * Integer.BYTES).asIntBuffer().get(ints);
        return ints;
    }

    public static long[] readLongs(ReadableByteChannel channel, int count) throws IOException {
        long[] longs = new long[count];
        readBuffer(channel, count * Long.BYTES).asLongBuffer().get(longs);
        return longs;
    }

    public static String readString(SeekableByteChannel channel, int size) throws IOException {
        return StandardCharsets.US_ASCII.newDecoder()
            .decode(readBuffer(channel, size))
            .toString();
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
}
