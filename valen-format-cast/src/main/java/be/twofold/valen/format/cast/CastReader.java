package be.twofold.valen.format.cast;

import java.io.*;
import java.nio.*;
import java.util.*;

final class CastReader {
    private final BinaryReader reader;

    private CastReader(BinaryReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    static Cast read(InputStream in) throws IOException {
        try (BinaryReader reader = new BinaryReader(new BufferedInputStream(in))) {
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

        ArrayList<CastNode> rootNodes = new ArrayList<CastNode>(rootNodeCount);
        for (int i = 0; i < rootNodeCount; i++) {
            rootNodes.add(readNode());
        }
        return new Cast(rootNodes);
    }

    public CastNode readNode() throws IOException {
        CastNodeID identifier = CastNodeID.fromValue(reader.readInt());
        int nodeSize = reader.readInt();
        long nodeHash = reader.readLong();
        int propertyCount = reader.readInt();
        int childCount = reader.readInt();

        LinkedHashMap<String, CastProperty> properties = new LinkedHashMap<String, CastProperty>(propertyCount);
        for (int i = 0; i < propertyCount; i++) {
            CastProperty property = readProperty();
            properties.put(property.name(), property);
        }

        ArrayList<CastNode> children = new ArrayList<CastNode>(childCount);
        for (int i = 0; i < childCount; i++) {
            children.add(readNode());
        }

        return switch (identifier) {
            case ROOT -> new CastNode.Root(nodeHash, properties, children);
            case MODEL -> new CastNode.Model(nodeHash, properties, children);
            case MESH -> new CastNode.Mesh(nodeHash, properties, children);
            case HAIR -> new CastNode.Hair(nodeHash, properties, children);
            case BLEND_SHAPE -> new CastNode.BlendShape(nodeHash, properties, children);
            case SKELETON -> new CastNode.Skeleton(nodeHash, properties, children);
            case BONE -> new CastNode.Bone(nodeHash, properties, children);
            case IK_HANDLE -> new CastNode.IkHandle(nodeHash, properties, children);
            case CONSTRAINT -> new CastNode.Constraint(nodeHash, properties, children);
            case ANIMATION -> new CastNode.Animation(nodeHash, properties, children);
            case CURVE -> new CastNode.Curve(nodeHash, properties, children);
            case CURVE_MODE_OVERRIDE -> new CastNode.CurveModeOverride(nodeHash, properties, children);
            case NOTIFICATION_TRACK -> new CastNode.NotificationTrack(nodeHash, properties, children);
            case MATERIAL -> new CastNode.Material(nodeHash, properties, children);
            case FILE -> new CastNode.File(nodeHash, properties, children);
            case COLOR -> new CastNode.Color(nodeHash, properties, children);
            case INSTANCE -> new CastNode.Instance(nodeHash, properties, children);
            case METADATA -> new CastNode.Metadata(nodeHash, properties, children);
        };
    }

    private CastProperty readProperty() throws IOException {
        CastPropertyID identifier = CastPropertyID.fromValue(reader.readShort());
        short nameSize = reader.readShort();
        int arrayLength = reader.readInt();

        String name = reader.readString(nameSize);
        Object value = arrayLength == 1
            ? readSingle(identifier)
            : readArray(identifier, arrayLength);

        return new CastProperty(identifier, name, value);
    }

    private Object readSingle(CastPropertyID identifier) throws IOException {
        return switch (identifier) {
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
    }

    private Buffer readArray(CastPropertyID identifier, int arrayLength) throws IOException {
        ByteBuffer buffer = reader.readBuffer(arrayLength * identifier.size());
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
