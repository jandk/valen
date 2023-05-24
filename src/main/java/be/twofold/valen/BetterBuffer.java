package be.twofold.valen;

import be.twofold.valen.geometry.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

public final class BetterBuffer {
    private final ByteBuffer buffer;

    public BetterBuffer(ByteBuffer buffer) {
        this.buffer = Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("buffer must be little endian");
        }
    }

    public byte getByte() {
        return buffer.get();
    }

    public int getInt() {
        return buffer.getInt();
    }

    public float getFloat() {
        return buffer.getFloat();
    }

    public boolean getIntAsBool() {
        int value = getInt();
        return switch (value) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new IllegalStateException("Unexpected value for bool4: " + value);
        };
    }

    public byte[] getBytes(int size) {
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    public int[] getInts(int size) {
        int[] ints = new int[size];
        buffer.asIntBuffer().get(ints);
        skip(size * Integer.BYTES);
        return ints;
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

    public void skip(int size) {
        buffer.position(buffer.position() + size);
    }

    public void expectInt(int expected) {
        int value = getInt();
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
