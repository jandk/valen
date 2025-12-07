package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public interface BinaryReader extends Closeable {
    static BinaryReader fromBytes(Bytes bytes) {
        return new BytesBinaryReader(bytes);
    }

    static BinaryReader fromPath(Path path) throws IOException {
        return new ChannelBinaryReader(Files.newByteChannel(path, StandardOpenOption.READ));
    }

    void read(MutableBytes dst) throws IOException;

    long size();

    long position();

    BinaryReader position(long pos) throws IOException;

    @Override
    default void close() throws IOException {
    }

    default void skip(long count) throws IOException {
        this.position(position() + count);
    }

    byte readByte() throws IOException;

    default int readByteUnsigned() throws IOException {
        return Byte.toUnsignedInt(readByte());
    }

    short readShort() throws IOException;

    default int readShortUnsigned() throws IOException {
        return Short.toUnsignedInt(readShort());
    }

    int readInt() throws IOException;

    default long readIntUnsigned() throws IOException {
        return Integer.toUnsignedLong(readInt());
    }

    long readLong() throws IOException;

    default float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    default double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    default Bytes readBytes(int len) throws IOException {
        var result = MutableBytes.allocate(len);
        read(result);
        return result;
    }

    default short readShortBE() throws IOException {
        return Short.reverseBytes(readShort());
    }

    default Shorts readShorts(int count) throws IOException {
        var result = MutableShorts.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readShort());
        }
        return result;
    }

    default int readIntBE() throws IOException {
        return Integer.reverseBytes(readInt());
    }

    default Ints readInts(int count) throws IOException {
        var result = MutableInts.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readInt());
        }
        return result;
    }

    default long readLongBE() throws IOException {
        return Long.reverseBytes(readLong());
    }

    default Longs readLongs(int count) throws IOException {
        var result = MutableLongs.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readLong());
        }
        return result;
    }

    default Floats readFloats(int count) throws IOException {
        var result = MutableFloats.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readFloat());
        }
        return result;
    }

    default Doubles readDoubles(int count) throws IOException {
        var result = MutableDoubles.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readDouble());
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

    default Ints readLongsAsInts(int len) throws IOException {
        var result = MutableInts.allocate(len);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readLongAsInt());
        }
        return result;
    }

    default String readString(int length) throws IOException {
        return readString(length, StandardCharsets.UTF_8);
    }

    default String readString(int length, Charset charset) throws IOException {
        return charset.newDecoder().decode(readBytes(length).asBuffer()).toString();
    }

    default String readCString() throws IOException {
        var result = new ByteArrayOutputStream();
        while (true) {
            var b = readByte();
            if (b == 0) {
                break;
            }
            result.write(b);
        }
        return result.toString(StandardCharsets.UTF_8);
    }

    default String readPString() throws IOException {
        return readString(readInt());
    }

    default <T> T readObject(ObjectMapper<T> mapper) throws IOException {
        return mapper.read(this);
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

    default void expectPadding(int length) throws IOException {
        Check.positive(length, "length");
        for (int i = 0; i < length; i++) {
            expectByte((byte) 0);
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
