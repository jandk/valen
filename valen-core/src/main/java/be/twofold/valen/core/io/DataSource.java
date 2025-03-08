package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public interface DataSource extends Closeable {
    static DataSource fromArray(byte[] array) {
        return fromArray(array, 0, array.length);
    }

    static DataSource fromArray(byte[] array, int offset, int length) {
        return new ByteArrayDataSource(array, offset, length);
    }

    static DataSource fromBuffer(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            var dataSource = new ByteArrayDataSource(buffer.array(), buffer.arrayOffset(), buffer.limit());
            dataSource.position(buffer.position());
            return dataSource;
        }
        return new ByteBufferDataSource(buffer);
    }

    static DataSource fromPath(Path path) throws IOException {
        return new ChannelDataSource(Files.newByteChannel(path, StandardOpenOption.READ));
    }

    void read(ByteBuffer dst) throws IOException;

    long size();

    long position();

    void position(long pos) throws IOException;

    @Override
    default void close() throws IOException {
    }

    default void skip(long count) throws IOException {
        position(position() + count);
    }

    byte readByte() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    default ByteBuffer readBuffer(int len) throws IOException {
        var buffer = ByteBuffer.allocate(len);
        read(buffer);
        buffer.flip();
        return buffer;
    }

    default byte[] readBytes(int len) throws IOException {
        return Buffers.toArray(readBuffer(len));
    }

    default short readShortBE() throws IOException {
        return Short.reverseBytes(readShort());
    }

    default short[] readShorts(int count) throws IOException {
        var result = new short[count];
        for (var i = 0; i < result.length; i++) {
            result[i] = readShort();
        }
        return result;
    }

    default int readIntBE() throws IOException {
        return Integer.reverseBytes(readInt());
    }

    default int[] readInts(int count) throws IOException {
        var result = new int[count];
        for (var i = 0; i < result.length; i++) {
            result[i] = readInt();
        }
        return result;
    }

    default long readLongBE() throws IOException {
        return Long.reverseBytes(readLong());
    }

    default long[] readLongs(int count) throws IOException {
        var result = new long[count];
        for (var i = 0; i < result.length; i++) {
            result[i] = readLong();
        }
        return result;
    }

    default float[] readFloats(int count) throws IOException {
        var result = new float[count];
        for (var i = 0; i < result.length; i++) {
            result[i] = readFloat();
        }
        return result;
    }

    default double[] readDoubles(int count) throws IOException {
        var result = new double[count];
        for (var i = 0; i < result.length; i++) {
            result[i] = readDouble();
        }
        return result;
    }

    default boolean readBoolByte() throws IOException {
        var value = readByte();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IOException("Unexpected value for bool: " + value);
        };
    }

    default boolean readBoolInt() throws IOException {
        var value = readInt();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IOException("Unexpected value for bool: " + value);
        };
    }

    default int readLongAsInt() throws IOException {
        return Math.toIntExact(readLong());
    }

    default int[] readLongsAsInts(int count) throws IOException {
        var result = new int[count];
        for (var i = 0; i < count; i++) {
            result[i] = readLongAsInt();
        }
        return result;
    }

    default String readString(int length) throws IOException {
        return StandardCharsets.UTF_8.decode(readBuffer(length)).toString();
    }

    default String readCString() throws IOException {
        var result = new StringBuilder();
        while (true) {
            var b = readByte();
            if (b == 0) {
                break;
            }
            result.append((char) b);
        }
        return result.toString();
    }

    default String readPString() throws IOException {
        return readString(readInt());
    }

    default <T> List<T> readObjects(int count, ObjectMapper<T> mapper) throws IOException {
        var result = new ArrayList<T>(count);
        for (var i = 0; i < count; i++) {
            result.add(mapper.read(this));
        }
        return List.copyOf(result);
    }

    default void expectByte(byte expected) throws IOException {
        var actual = readByte();
        if (actual != expected) {
            throw new IOException("Expected byte " + expected + ", but got " + actual);
        }
    }

    default void expectShort(short expected) throws IOException {
        var actual = readShort();
        if (actual != expected) {
            throw new IOException("Expected short " + expected + ", but got " + actual);
        }
    }

    default void expectInt(int expected) throws IOException {
        var actual = readInt();
        if (actual != expected) {
            throw new IOException("Expected int " + expected + ", but got " + actual);
        }
    }

    default void expectLong(long expected) throws IOException {
        var actual = readLong();
        if (actual != expected) {
            throw new IOException("Expected long " + expected + ", but got " + actual);
        }
    }

    default void expectPosition(long expected) throws IOException {
        var actual = position();
        if (actual != expected) {
            throw new IOException("Expected position " + expected + ", but got " + actual);
        }
    }

    default void expectEnd() throws IOException {
        if (position() != size()) {
            throw new IOException("Expected end of file, but got " + position() + " of " + size());
        }
    }
}
