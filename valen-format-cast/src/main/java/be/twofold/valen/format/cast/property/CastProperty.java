package be.twofold.valen.format.cast.property;

import be.twofold.valen.format.cast.*;
import be.twofold.valen.format.cast.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record CastProperty(
    CastPropertyID identifier,
    String name,
    Object value
) {
    public CastProperty {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
    }

    public static CastProperty single(CastPropertyID identifier, String name, Object value) {
        return new CastProperty(identifier, name, value);
    }

    public static CastProperty string(String name, String value) {
        return single(CastPropertyID.STRING, name, value);
    }

    public static CastProperty readProperty(BinaryReader reader) throws IOException {
        var identifier = CastPropertyID.fromValue(reader.readShort());
        var nameSize = reader.readShort();
        var arrayLength = reader.readInt();

        String name = reader.readString(nameSize);

        if (arrayLength == 1) {
            var value = switch (identifier) {
                case BYTE -> reader.readByte();
                case SHORT -> reader.readShort();
                case INT -> reader.readInt();
                case LONG -> reader.readLong();
                case FLOAT -> reader.readFloat();
                case DOUBLE -> reader.readDouble();
                case STRING -> reader.readCString();
                case VECTOR2 -> {
                    float x = reader.readFloat();
                    float y = reader.readFloat();
                    yield new Vec2(x, y);
                }
                case VECTOR3 -> {
                    float x = reader.readFloat();
                    float y = reader.readFloat();
                    float z = reader.readFloat();
                    yield new Vec3(x, y, z);
                }
                case VECTOR4 -> {
                    float x = reader.readFloat();
                    float y = reader.readFloat();
                    float z = reader.readFloat();
                    float w = reader.readFloat();
                    yield new Vec4(x, y, z, w);
                }
            };
            return single(identifier, name, value);
        }

        Buffer value = readArray(reader, identifier, arrayLength);
        return new CastProperty(identifier, name, value);
    }

    private static Buffer readArray(BinaryReader reader, CastPropertyID identifier, int arrayLength) throws IOException {
        var buffer = reader.readBuffer(arrayLength * identifier.size());
        return switch (identifier) {
            case BYTE -> buffer;
            case SHORT -> buffer.asShortBuffer();
            case INT -> buffer.asIntBuffer();
            case LONG -> buffer.asLongBuffer();
            case FLOAT, VECTOR2, VECTOR3, VECTOR4 -> buffer.asFloatBuffer();
            case DOUBLE -> buffer.asDoubleBuffer();
            case STRING -> throw new UnsupportedOperationException();
        };
    }

    public int length() {
        return 8 + Utf8.length(name) + arrayLength() * identifier.size();
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
