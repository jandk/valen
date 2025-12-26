package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public abstract class BinarySource implements Closeable {
    final long size;
    boolean bigEndian;

    BinarySource(long size) {
        this.size = Check.positiveOrZero(size, "size");
        this.bigEndian = false;
    }

    public static BinarySource open(Path path) throws IOException {
        return new ChannelBinarySource(Files.newByteChannel(path));
    }

    public static BinarySource wrap(Bytes bytes) throws IOException {
        return new BytesBinarySource(bytes);
    }


    public final ByteOrder order() {
        return bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public final BinarySource order(ByteOrder order) {
        this.bigEndian = Check.nonNull(order, "order") == ByteOrder.BIG_ENDIAN;
        return this;
    }

    public final long size() {
        return size;
    }

    public final long remaining() {
        return size - position();
    }

    public final void skip(long count) throws IOException {
        position(Math.addExact(position(), Check.positiveOrZero(count, "count")));
    }

    public abstract long position();

    public abstract BinarySource position(long position);

    public abstract void readBytes(Bytes.Mutable target) throws IOException;

    public abstract byte readByte() throws IOException;

    public abstract short readShort() throws IOException;

    public abstract int readInt() throws IOException;

    public abstract long readLong() throws IOException;

    public final int readLongAsInt() throws IOException {
        return Math.toIntExact(readLong());
    }

    public final float readHalf() throws IOException {
        return Float.float16ToFloat(readShort());
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final Bytes readBytes(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Bytes.empty();
        }
        ensureRemaining(count * (long) Byte.BYTES);

        var result = Bytes.Mutable.allocate(count);
        readBytes(result);
        return result;
    }

    public final Shorts readShorts(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Shorts.empty();
        }
        ensureRemaining(count * (long) Short.BYTES);

        var result = Shorts.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readShort());
        }
        return result;
    }

    public final Ints readInts(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Ints.empty();
        }
        ensureRemaining(count * (long) Integer.BYTES);

        var result = Ints.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readInt());
        }
        return result;
    }

    public final Longs readLongs(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Longs.empty();
        }
        ensureRemaining(count * (long) Long.BYTES);

        var result = Longs.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readLong());
        }
        return result;
    }

    public final Ints readLongsAsInts(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Ints.empty();
        }
        ensureRemaining(count * (long) Long.BYTES);

        var result = Ints.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readLongAsInt());
        }
        return result;
    }

    public final Floats readHalfs(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Floats.empty();
        }
        ensureRemaining(count * (long) Short.BYTES);

        var result = Floats.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readHalf());
        }
        return result;
    }

    public final Floats readFloats(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Floats.empty();
        }
        ensureRemaining(count * (long) Float.BYTES);

        var result = Floats.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readFloat());
        }
        return result;
    }

    public final Doubles readDoubles(int count) throws IOException {
        if (Check.positiveOrZero(count, "count") == 0) {
            return Doubles.empty();
        }
        ensureRemaining(count * (long) Double.BYTES);

        var result = Doubles.Mutable.allocate(count);
        for (var i = 0; i < result.length(); i++) {
            result.set(i, readDouble());
        }
        return result;
    }


    public final boolean readBool(BoolFormat format) throws IOException {
        var value = switch (format) {
            case BYTE -> readByte();
            case SHORT -> readShort();
            case INT -> readInt();
        };
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IOException("Unexpected value for bool: " + value);
        };
    }

    public final String readString(StringFormat format) throws IOException {
        return readString(format, StandardCharsets.UTF_8);
    }

    public final String readString(StringFormat format, Charset charset) throws IOException {
        return switch (format) {
            case BYTE_LENGTH -> readString(Byte.toUnsignedInt(readByte()), charset);
            case SHORT_LENGTH -> readString(Short.toUnsignedInt(readShort()), charset);
            case INT_LENGTH -> readString(readInt(), charset);
            case NULL_TERM -> readNullTerminatedString(charset);
        };
    }

    public final String readString(int length) throws IOException {
        return readString(length, StandardCharsets.UTF_8);
    }

    public final String readString(int length, Charset charset) throws IOException {
        if (Check.positiveOrZero(length, "length") == 0) {
            return "";
        }
        return readBytes(length).toString(charset);
    }

    public final List<String> readStrings(int count, StringFormat format) throws IOException {
        return readStrings(count, format, StandardCharsets.UTF_8);
    }

    public final List<String> readStrings(int count, StringFormat format, Charset charset) throws IOException {
        return readObjects(count, reader -> reader.readString(format, charset));
    }

    public final <T> T readObject(Mapper<T> mapper) throws IOException {
        return mapper.read(this);
    }

    public final <T> List<T> readObjects(int count, Mapper<T> mapper) throws IOException {
        List<T> result = new ArrayList<>(Check.positiveOrZero(count, "count"));
        for (var i = 0; i < count; i++) {
            result.add(mapper.read(this));
        }
        return List.copyOf(result);
    }


    public final void expectByte(byte expected) throws IOException {
        var actual = readByte();
        if (actual != expected) {
            throw new IOException("Expected byte " + expected + ", but got " + actual);
        }
    }

    public final void expectShort(short expected) throws IOException {
        var actual = readShort();
        if (actual != expected) {
            throw new IOException("Expected short " + expected + ", but got " + actual);
        }
    }

    public final void expectInt(int expected) throws IOException {
        var actual = readInt();
        if (actual != expected) {
            throw new IOException("Expected int " + expected + ", but got " + actual);
        }
    }

    public final void expectLong(long expected) throws IOException {
        var actual = readLong();
        if (actual != expected) {
            throw new IOException("Expected long " + expected + ", but got " + actual);
        }
    }

    public final void expectFloat(float expected) throws IOException {
        var actual = readFloat();
        if (Float.compare(actual, expected) != 0) {
            throw new IOException("Expected float " + expected + ", but got " + actual);
        }
    }

    public final void expectDouble(double expected) throws IOException {
        var actual = readDouble();
        if (Double.compare(actual, expected) != 0) {
            throw new IOException("Expected double " + expected + ", but got " + actual);
        }
    }

    public final void expectEnd() throws IOException {
        if (remaining() > 0) {
            throw new IOException("Expected end of stream, but " + remaining() + " bytes remain");
        }
    }

    public final void ensureRemaining(long expected) throws IOException {
        if (remaining() < expected) {
            throw new IOException("Expected at least " + expected + " bytes remaining, but only " + remaining() + " are available");
        }
    }

    private String readNullTerminatedString(Charset charset) throws IOException {
        return (switch (charset.name()) {
            case "UTF-16", "UTF-16BE", "UTF-16LE" -> readNullTerminatedString2();
            case "UTF-32", "UTF-32BE", "UTF-32LE" -> readNullTerminatedString4();
            default -> readNullTerminatedString1();
        }).toString(charset);
    }

    private ByteArrayOutputStream readNullTerminatedString1() throws IOException {
        var result = new ByteArrayOutputStream();
        while (true) {
            var b0 = readByte();
            if (b0 == 0) {
                break;
            }
            result.write(b0);
        }
        return result;
    }

    private ByteArrayOutputStream readNullTerminatedString2() throws IOException {
        var result = new ByteArrayOutputStream();
        while (true) {
            var b0 = readByte();
            var b1 = readByte();
            if (b0 == 0 && b1 == 0) {
                break;
            }
            result.write(b0);
            result.write(b1);
        }
        return result;
    }

    private ByteArrayOutputStream readNullTerminatedString4() throws IOException {
        var result = new ByteArrayOutputStream();
        while (true) {
            var b0 = readByte();
            var b1 = readByte();
            var b2 = readByte();
            var b3 = readByte();
            if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 0) {
                break;
            }
            result.write(b0);
            result.write(b1);
            result.write(b2);
            result.write(b3);
        }
        return result;
    }

    @FunctionalInterface
    public interface Mapper<T> {
        T read(BinarySource source) throws IOException;
    }
}
