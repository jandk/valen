package be.twofold.valen.format.cast;

import java.io.*;
import java.util.*;

public sealed interface CastValue {
    static CastValue read(BinaryReader reader, CastPropertyID type) throws IOException {
        return switch (type) {
            case BYTE -> new CastByte(reader.readByte());
            case SHORT -> new CastShort(reader.readShort());
            case INT -> new CastInt(reader.readInt());
            case LONG -> new CastLong(reader.readLong());
            case FLOAT -> new CastFloat(reader.readFloat());
            case DOUBLE -> new CastDouble(reader.readDouble());
            case STRING -> new CastString(reader.readCString());
            case VECTOR2 -> new CastVector2(
                reader.readFloat(),
                reader.readFloat()
            );
            case VECTOR3 -> new CastVector3(
                reader.readFloat(),
                reader.readFloat(),
                reader.readFloat()
            );
            case VECTOR4 -> new CastVector4(
                reader.readFloat(),
                reader.readFloat(),
                reader.readFloat(),
                reader.readFloat()
            );
        };
    }
//
//    default Optional<String> asString() {
//        if (!(this instanceof CastString cs)) {
//            return Optional.empty();
//        }
//        return Optional.of(cs.value());
//    }
//
//    default Optional<Number> asNumber() {
//        return switch (this) {
//            case CastByte(var value) -> Optional.of(value);
//            case CastShort(var value) -> Optional.of(value);
//            case CastInt(var value) -> Optional.of(value);
//            case CastLong(var value) -> Optional.of(value);
//            case CastFloat(var value) -> Optional.of(value);
//            case CastDouble(var value) -> Optional.of(value);
//            default -> Optional.empty();
//        };
//    }
//
//    default Optional<CastVector2> asVector2() {
//        if (!(this instanceof CastVector2 cv)) {
//            return Optional.empty();
//        }
//        return Optional.of(cv);
//    }
//
//    default Optional<CastVector3> asVector3() {
//        if (!(this instanceof CastVector3 cv)) {
//            return Optional.empty();
//        }
//        return Optional.of(cv);
//    }
//
//    default Optional<CastVector4> asVector4() {
//        if (!(this instanceof CastVector4 cv)) {
//            return Optional.empty();
//        }
//        return Optional.of(cv);
//    }

    record CastByte(byte value) implements CastValue {
        @Override
        public String toString() {
            return String.valueOf(Byte.toUnsignedInt(value));
        }
    }

    record CastShort(short value) implements CastValue {
        @Override
        public String toString() {
            return String.valueOf(Short.toUnsignedInt(value));
        }
    }

    record CastInt(int value) implements CastValue {
        @Override
        public String toString() {
            return Integer.toUnsignedString(value);
        }
    }

    record CastLong(long value) implements CastValue {
        @Override
        public String toString() {
            return Long.toUnsignedString(value);
        }
    }

    record CastFloat(float value) implements CastValue {
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    record CastDouble(double value) implements CastValue {
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    record CastString(String value) implements CastValue {
        public CastString {
            Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    record CastVector2(
        float x,
        float y
    ) implements CastValue {
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    record CastVector3(
        float x,
        float y,
        float z
    ) implements CastValue {
        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }

    record CastVector4(
        float x,
        float y,
        float z,
        float w
    ) implements CastValue {
        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ", " + w + ")";
        }
    }
}
