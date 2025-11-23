package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.util.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

final class GdexReader {
    private final BinaryReader reader;

    GdexReader(BinaryReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    Gdex read() throws IOException {
        var tag = GdexItemTag.read(reader);
        var type = GdexItemType.read(reader);
        var flags = GdexItemFlags.read(reader);
        var size = Short.toUnsignedInt(reader.readShort());
        if (flags == GdexItemFlags.LONG_HEADER) {
            size = Math.toIntExact(size | Integer.toUnsignedLong(reader.readInt()) << 16);
        }

        var result = read(type, tag, size);
        reader.position((reader.position() + 3) & ~3);
        return result;
    }

    private Gdex read(GdexItemType type, GdexItemTag tag, int size) throws IOException {
        return switch (type) {
            case RAW -> {
                var value = reader.readBytesStruct(Math.toIntExact(size));
                yield new GdexRaw(tag, value);
            }
            case STRUCT -> {
                var position = reader.position();
                var values = new ArrayList<Gdex>();
                while (reader.position() < position + size) {
                    values.add(read());
                }
                if (reader.position() > position + size) {
                    throw new IOException("Read past end of struct");
                }
                yield new GdexStruct(tag, values);
            }
            case STRING -> {
                var value = reader.readString(size - 2, StandardCharsets.UTF_16LE);
                reader.skip(2);
                yield new GdexString(tag, value);
            }
            case INT32 -> {
                verifySize(size, Integer.BYTES, "int32");
                yield new GdexInt32(tag, reader.readInt());
            }
            case INT64 -> {
                verifySize(size, Long.BYTES, "int64");
                yield new GdexInt64(tag, reader.readLong());
            }
            case FLOAT -> {
                verifySize(size, Float.BYTES, "float");
                yield new GdexFloat(tag, reader.readFloat());
            }
            case DOUBLE -> {
                verifySize(size, Double.BYTES, "double");
                yield new GdexDouble(tag, reader.readDouble());
            }
            case DATE -> {
                var epoch = 630822816000000000L; // 2000-01-01
                var value = DotNetUtils.ticksToInstant(epoch + reader.readLong());
                yield new GdexDate(tag, value);
            }
            case INT32_ARRAY -> {
                var count = verifyArraySize(size, Integer.BYTES, "int32_array");
                var values = reader.readObjects(count, BinaryReader::readInt);
                yield new GdexInt32Array(tag, values);
            }
            case FLOAT_ARRAY -> {
                var count = verifyArraySize(size, Float.BYTES, "float_array");
                var values = reader.readObjects(count, BinaryReader::readFloat);
                yield new GdexFloatArray(tag, values);
            }
            case INT64_ARRAY -> {
                var count = verifyArraySize(size, Long.BYTES, "int64_array");
                var values = reader.readObjects(count, BinaryReader::readLong);
                yield new GdexInt64Array(tag, values);
            }
            case DOUBLE_ARRAY -> {
                var count = verifyArraySize(size, Double.BYTES, "double_array");
                var values = reader.readObjects(count, BinaryReader::readDouble);
                yield new GdexDoubleArray(tag, values);
            }
            case GUID -> {
                verifySize(size, 16, "guid");
                var value = DotNetUtils.guidBytesToUUID(reader.readBytesStruct(16));
                yield new GdexGuid(tag, value);
            }
            case GUID_ARRAY -> {
                var count = verifyArraySize(size, 16, "guid_array");
                var values = reader.readObjects(count, r -> DotNetUtils.guidBytesToUUID(r.readBytesStruct(16)));
                yield new GdexGuidArray(tag, values);
            }
        };
    }

    private static int verifyArraySize(int size, int elementSize, String name) throws IOException {
        var count = size / elementSize;
        verifySize(count * elementSize, size, name);
        return count;
    }

    private static void verifySize(int size, int elementSize, String name) throws IOException {
        if (size != elementSize) {
            throw new IOException("Invalid size for " + name);
        }
    }
}
