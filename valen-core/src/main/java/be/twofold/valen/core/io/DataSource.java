package be.twofold.valen.core.io;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public abstract class DataSource implements AutoCloseable {

    public static DataSource fromArray(byte[] array) {
        return new ByteArrayDataSource(array);
    }

    public static DataSource fromBuffer(ByteBuffer buffer) {
        Check.argument(buffer.hasArray(), "ByteBuffer must be backed by an array");
        return new ByteArrayDataSource(buffer.array(), buffer.arrayOffset(), buffer.limit());
    }

    public static DataSource fromPath(Path path) throws IOException {
        return new ChannelDataSource(Files.newByteChannel(path, StandardOpenOption.READ));
    }

    public static DataSource fromStream(InputStream stream) {
        return new InputStreamDataSource(stream);
    }

    public abstract byte readByte() throws IOException;

    public void readBytes(byte[] dst, int off, int len) throws IOException {
        readBytes(dst, off, len, true);
    }

    public abstract void readBytes(byte[] dst, int off, int len, boolean buffered) throws IOException;

    public abstract long tell();

    public abstract void seek(long pos) throws IOException;

    public abstract long size();

    public abstract void close() throws IOException;

    public void skip(long count) throws IOException {
        seek(tell() + count);
    }

    public byte[] readBytes(int len) throws IOException {
        var result = new byte[len];
        readBytes(result, 0, len);
        return result;
    }

    public short readShort() throws IOException {
        var b0 = Byte.toUnsignedInt(readByte());
        var b1 = Byte.toUnsignedInt(readByte());
        return (short) (b0 | (b1 << 8));
    }

    public short readShortBE() throws IOException {
        return Short.reverseBytes(readShort());
    }

    public void readShorts(short[] array, int offset, int length) throws IOException {
        Objects.checkFromIndexSize(offset, length, array.length);
        for (var i = 0; i < length; i++) {
            array[offset + i] = readShort();
        }
    }

    public short[] readShorts(int count) throws IOException {
        var result = new short[count];
        readShorts(result, 0, count);
        return result;
    }

    public int readInt() throws IOException {
        var b0 = Byte.toUnsignedInt(readByte());
        var b1 = Byte.toUnsignedInt(readByte());
        var b2 = Byte.toUnsignedInt(readByte());
        var b3 = Byte.toUnsignedInt(readByte());
        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    public int readIntBE() throws IOException {
        return Integer.reverseBytes(readInt());
    }

    public void readInts(int[] array, int offset, int length) throws IOException {
        Objects.checkFromIndexSize(offset, length, array.length);
        for (var i = 0; i < length; i++) {
            array[offset + i] = readInt();
        }
    }

    public int[] readInts(int count) throws IOException {
        var result = new int[count];
        readInts(result, 0, count);
        return result;
    }

    public long readLong() throws IOException {
        var b0 = Byte.toUnsignedLong(readByte());
        var b1 = Byte.toUnsignedLong(readByte());
        var b2 = Byte.toUnsignedLong(readByte());
        var b3 = Byte.toUnsignedLong(readByte());
        var b4 = Byte.toUnsignedLong(readByte());
        var b5 = Byte.toUnsignedLong(readByte());
        var b6 = Byte.toUnsignedLong(readByte());
        var b7 = Byte.toUnsignedLong(readByte());
        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24) | (b4 << 32) | (b5 << 40) | (b6 << 48) | (b7 << 56);
    }

    public long readLongBE() throws IOException {
        return Long.reverseBytes(readLong());
    }

    public void readLongs(long[] array, int offset, int length) throws IOException {
        Objects.checkFromIndexSize(offset, length, array.length);
        for (var i = 0; i < length; i++) {
            array[offset + i] = readLong();
        }
    }

    public long[] readLongs(int count) throws IOException {
        var result = new long[count];
        readLongs(result, 0, count);
        return result;
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void readFloats(float[] array, int offset, int length) throws IOException {
        Objects.checkFromIndexSize(offset, length, array.length);
        for (var i = 0; i < length; i++) {
            array[offset + i] = readFloat();
        }
    }

    public float[] readFloats(int count) throws IOException {
        var result = new float[count];
        readFloats(result, 0, count);
        return result;
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public void readDoubles(double[] array, int offset, int length) throws IOException {
        Objects.checkFromIndexSize(offset, length, array.length);
        for (var i = 0; i < length; i++) {
            array[offset + i] = readDouble();
        }
    }

    public double[] readDoubles(int count) throws IOException {
        var result = new double[count];
        readDoubles(result, 0, count);
        return result;
    }

    //
    // Custom read methods
    //

    public boolean readBoolByte() throws IOException {
        var value = readByte();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IOException("Unexpected value for bool: " + value);
        };
    }

    public boolean readBoolInt() throws IOException {
        var value = readInt();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IOException("Unexpected value for bool: " + value);
        };
    }

    public int readLongAsInt() throws IOException {
        return Math.toIntExact(readLong());
    }

    public int[] readLongsAsInts(int count) throws IOException {
        var result = new int[count];
        for (var i = 0; i < count; i++) {
            result[i] = readLongAsInt();
        }
        return result;
    }

    public String readString(int length) throws IOException {
        return new String(readBytes(length), StandardCharsets.UTF_8);
    }

    public String readCString() throws IOException {
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

    public String readPString() throws IOException {
        return readString(readInt());
    }

    public <T> List<T> readStructs(int count, StructMapper<T> mapper) throws IOException {
        var result = new ArrayList<T>(count);
        for (var i = 0; i < count; i++) {
            result.add(mapper.read(this));
        }
        return List.copyOf(result);
    }

    public void expectByte(byte expected) throws IOException {
        var actual = readByte();
        if (actual != expected) {
            throw new IOException("Expected " + expected + ", but got " + actual);
        }
    }

    public void expectShort(short expected) throws IOException {
        var actual = readShort();
        if (actual != expected) {
            throw new IOException("Expected " + expected + ", but got " + actual);
        }
    }

    public void expectInt(int expected) throws IOException {
        var actual = readInt();
        if (actual != expected) {
            throw new IOException("Expected " + expected + ", but got " + actual);
        }
    }

    public void expectLong(long expected) throws IOException {
        var actual = readLong();
        if (actual != expected) {
            throw new IOException("Expected " + expected + ", but got " + actual);
        }
    }

    public void expectPosition(long expected) throws IOException {
        var actual = tell();
        if (actual != expected) {
            throw new IOException("Expected position " + expected + ", but got " + actual);
        }
    }

    public void expectEnd() throws IOException {
        if (tell() != size()) {
            throw new IOException("Expected end of file, but got " + tell() + " of " + size());
        }
    }

    //
    // Custom types
    //

    public Matrix3 readMatrix3() throws IOException {
        float m00 = readFloat();
        float m01 = readFloat();
        float m02 = readFloat();
        float m10 = readFloat();
        float m11 = readFloat();
        float m12 = readFloat();
        float m20 = readFloat();
        float m21 = readFloat();
        float m22 = readFloat();
        return new Matrix3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }

    public Quaternion readQuaternion() throws IOException {
        float x = readFloat();
        float y = readFloat();
        float z = readFloat();
        float w = readFloat();
        return new Quaternion(x, y, z, w);
    }

    public Vector2 readVector2() throws IOException {
        float x = readFloat();
        float y = readFloat();
        return new Vector2(x, y);
    }

    public Vector3 readVector3() throws IOException {
        float x = readFloat();
        float y = readFloat();
        float z = readFloat();
        return new Vector3(x, y, z);
    }

    public Vector4 readVector4() throws IOException {
        float x = readFloat();
        float y = readFloat();
        float z = readFloat();
        float w = readFloat();
        return new Vector4(x, y, z, w);
    }
}
