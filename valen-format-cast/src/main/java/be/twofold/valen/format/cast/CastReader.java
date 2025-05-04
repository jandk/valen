package be.twofold.valen.format.cast;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class CastReader {
    private final BinaryReader reader;

    private CastReader(BinaryReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    public static Cast read(Path path) throws IOException {
        try (var reader = new BinaryReader(new BufferedInputStream(Files.newInputStream(path)))) {
            return new CastReader(reader).read();
        }
    }

    private Cast read() throws IOException {
        int magic = reader.readInt();
        if (magic != 0x74736163) {
            throw new IOException("Invalid magic number");
        }

        int version = reader.readInt();
        if (version != 1) {
            throw new IOException("Invalid version");
        }

        int rootNodeCount = reader.readInt();
        int flags = reader.readInt();
        if (flags != 0) {
            throw new IOException("Invalid flags");
        }

        var rootNodes = new ArrayList<CastNode>(rootNodeCount);
        for (int i = 0; i < rootNodeCount; i++) {
            rootNodes.add(readNode());
        }
        return new Cast(rootNodes);
    }

    private CastNode readNode() throws IOException {
        var identifier = CastNodeID.fromValue(reader.readInt());
        var nodeSize = reader.readInt();
        var nodeHash = reader.readLong();
        var propertyCount = reader.readInt();
        var childCount = reader.readInt();

        var properties = new ArrayList<CastProperty>(propertyCount);
        for (int i = 0; i < propertyCount; i++) {
            properties.add(readProperty());
        }

        var children = new ArrayList<CastNode>(childCount);
        for (int i = 0; i < childCount; i++) {
            children.add(readNode());
        }

        return new CastNode(identifier, nodeHash, properties, children);
    }

    private CastProperty readProperty() throws IOException {
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
            return CastProperty.single(identifier, name, value);
        }

        Buffer value = readArray(identifier, arrayLength);
        return CastProperty.array(identifier, name, value);
    }

    private Buffer readArray(CastPropertyID identifier, int arrayLength) throws IOException {
        var buffer = reader.readBuffer(arrayLength * identifier.elementSize());
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
}
