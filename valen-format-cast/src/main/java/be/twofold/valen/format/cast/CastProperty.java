package be.twofold.valen.format.cast;

import java.nio.*;
import java.util.Arrays;
import java.util.Objects;

public class CastProperty {
    private final CastPropertyID identifier;
    private final String name;
    private final Object value;

    private CastProperty(CastPropertyID identifier, String name, Object value) {
        this.identifier = Objects.requireNonNull(identifier);
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        check(identifier, value);
    }

    private void check(CastPropertyID identifier, Object value) {
        switch (identifier) {
            case BYTE -> checkType(value, 1, Byte.class, ByteBuffer.class);
            case SHORT -> checkType(value, 1, Short.class, ShortBuffer.class);
            case INT -> checkType(value, 1, Integer.class, IntBuffer.class);
            case LONG -> checkType(value, 1, Long.class, LongBuffer.class);
            case FLOAT -> checkType(value, 1, Float.class, FloatBuffer.class);
            case DOUBLE -> checkType(value, 1, Double.class, DoubleBuffer.class);
            case STRING -> checkType(value, 1, String.class);
            case VECTOR2 -> checkType(value, 2, Vec2.class, FloatBuffer.class);
            case VECTOR3 -> checkType(value, 3, Vec3.class, FloatBuffer.class);
            case VECTOR4 -> checkType(value, 4, Vec4.class, FloatBuffer.class);
        }
    }

    private void checkType(Object value, int stride, Class<?>... types) {
        for (Class<?> type : types) {
            if (type.isInstance(value)) {
                if (value instanceof Buffer buffer && buffer.capacity() % stride != 0) {
                    throw new IllegalArgumentException("Buffer capacity must be a multiple of " + stride + " but was " + buffer.capacity());
                }
                return;
            }
        }
        throw new IllegalArgumentException("Expected any of " + Arrays.toString(types) + " but got " + value.getClass());
    }

    public static CastProperty single(CastPropertyID identifier, String name, Object value) {
        return new CastProperty(identifier, name, value);
    }

    public static CastProperty string(String name, String value) {
        return single(CastPropertyID.STRING, name, value);
    }

    public static CastProperty array(CastPropertyID identifier, String name, Buffer buffer) {
        return new CastProperty(identifier, name, buffer);
    }

    public static CastProperty integral(String name, Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer _ -> array(CastPropertyID.BYTE, name, buffer);
            case ShortBuffer _ -> array(CastPropertyID.SHORT, name, buffer);
            case IntBuffer _ -> array(CastPropertyID.INT, name, buffer);
            default -> throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass());
        };
    }

    public CastPropertyID identifier() {
        return identifier;
    }

    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }

    public int length() {
        var length = 8 + Utf8.length(name);
        length += arrayLength() * switch (identifier) {
            case BYTE -> Byte.BYTES;
            case SHORT -> Short.BYTES;
            case INT -> Integer.BYTES;
            case LONG -> Long.BYTES;
            case FLOAT -> Float.BYTES;
            case DOUBLE -> Double.BYTES;
            case STRING -> 0;
            case VECTOR2 -> 2 * Float.BYTES;
            case VECTOR3 -> 3 * Float.BYTES;
            case VECTOR4 -> 4 * Float.BYTES;
        };
        return length;
    }

    public int arrayLength() {
        if (!(value instanceof Buffer)) {
            return 1;
        }

        int stride = switch (identifier) {
            case VECTOR2 -> 2;
            case VECTOR3 -> 3;
            case VECTOR4 -> 4;
            default -> 1;
        };
        return ((Buffer) value).capacity() / stride;
    }

    public Buffer asBuffer() {
        if (!(value instanceof Buffer buffer)) {
            throw new ClassCastException("Expected a buffer but got " + value.getClass());
        }
        return buffer;
    }

    public String asString() {
        if (!(value instanceof String string)) {
            throw new ClassCastException("Expected a string but got " + value.getClass());
        }
        return string;
    }
}
