package be.twofold.valen;

import be.twofold.valen.geometry.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class BetterBuffer {
    private final ByteBuffer buffer;

    public BetterBuffer(ByteBuffer buffer) {
        this.buffer = Objects
            .requireNonNull(buffer, "buffer is null")
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static BetterBuffer wrap(byte[] bytes) {
        return new BetterBuffer(ByteBuffer.wrap(bytes));
    }

    public byte getByte() {
        return buffer.get();
    }

    public short getShort() {
        return buffer.getShort();
    }

    public int getInt() {
        return buffer.getInt();
    }

    public long getLong() {
        return buffer.getLong();
    }

    public float getFloat() {
        return buffer.getFloat();
    }

    public boolean getByteAsBool() {
        byte value = getByte();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IllegalStateException("Unexpected value for bool1: " + value);
        };
    }

    public boolean getIntAsBool() {
        int value = getInt();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IllegalStateException("Unexpected value for bool4: " + value);
        };
    }

    public int getLongAsInt() {
        return Math.toIntExact(getLong());
    }

    public byte[] getBytes(int count) {
        byte[] bytes = new byte[count];
        buffer.get(bytes);
        return bytes;
    }

    public short[] getShorts(int count) {
        short[] shorts = new short[count];
        buffer.asShortBuffer().get(shorts);
        skip(count * Short.BYTES);
        return shorts;
    }

    public int[] getInts(int count) {
        int[] ints = new int[count];
        buffer.asIntBuffer().get(ints);
        skip(count * Integer.BYTES);
        return ints;
    }

    public long[] getLongs(int count) {
        long[] longs = new long[count];
        buffer.asLongBuffer().get(longs);
        skip(count * Long.BYTES);
        return longs;
    }

    public String getString() {
        int size = getInt();
        return new String(getBytes(size), StandardCharsets.US_ASCII);
    }

    public Vector2 getVector2() {
        float x = getFloat();
        float y = getFloat();
        return new Vector2(x, y);
    }

    public Vector3 getVector3() {
        float x = getFloat();
        float y = getFloat();
        float z = getFloat();
        return new Vector3(x, y, z);
    }

    public Vector4 getVector4() {
        float x = getFloat();
        float y = getFloat();
        float z = getFloat();
        float w = getFloat();
        return new Vector4(x, y, z, w);
    }

    public <T> List<T> getStructs(int count, Function<BetterBuffer, T> reader) {
        return Stream.generate(() -> reader.apply(this))
            .limit(count)
            .toList();
    }

    public int position() {
        return buffer.position();
    }

    public void position(int newPosition) {
        buffer.position(newPosition);
    }

    public void skip(int size) {
        buffer.position(position() + size);
    }

    public void expectByte(int expected) {
        byte value = getByte();
        if (value != expected) {
            throw new IllegalStateException("Expected " + expected + ", but got " + value);
        }
    }

    public void expectShort(int expected) {
        short value = getShort();
        if (value != expected) {
            throw new IllegalStateException("Expected " + expected + ", but got " + value);
        }
    }

    public void expectInt(int expected) {
        int value = getInt();
        if (value != expected) {
            throw new IllegalStateException("Expected " + expected + ", but got " + value);
        }
    }

    public void expectLong(long expected) {
        long value = getLong();
        if (value != expected) {
            throw new IllegalStateException("Expected " + expected + ", but got " + value);
        }
    }

    public void expectEnd() {
        if (buffer.hasRemaining()) {
            throw new IllegalStateException("Expected end of buffer, but got " + buffer.remaining() + " bytes left");
        }
    }
}
