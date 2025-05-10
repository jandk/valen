package be.twofold.valen.format.cast.node;

import be.twofold.valen.format.cast.io.*;
import be.twofold.valen.format.cast.property.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public abstract class CastNode {
    private final CastNodeID identifier;
    private final long hash;
    final AtomicLong hasher;
    final Map<String, CastProperty> properties;
    final List<CastNode> children;

    CastNode(CastNodeID identifier, AtomicLong hasher) {
        this(identifier, hasher.getAndIncrement(), hasher, new LinkedHashMap<>(), new ArrayList<>());
    }

    CastNode(CastNodeID identifier, long hash, Map<String, CastProperty> properties, List<CastNode> children) {
        this(identifier, hash, null, properties, children);
    }

    private CastNode(CastNodeID identifier, long hash, AtomicLong hasher, Map<String, CastProperty> properties, List<CastNode> children) {
        this.identifier = Objects.requireNonNull(identifier);
        this.hash = hash;
        this.hasher = hasher;
        this.properties = properties;
        this.children = children;
    }

    public static CastNode read(BinaryReader reader) throws IOException {
        var identifier = CastNodeID.fromValue(reader.readInt());
        var nodeSize = reader.readInt();
        var nodeHash = reader.readLong();
        var propertyCount = reader.readInt();
        var childCount = reader.readInt();

        var properties = new LinkedHashMap<String, CastProperty>(propertyCount);
        for (int i = 0; i < propertyCount; i++) {
            CastProperty property = CastProperty.readProperty(reader);
            properties.put(property.name(), property);
        }

        var children = new ArrayList<CastNode>(childCount);
        for (int i = 0; i < childCount; i++) {
            children.add(read(reader));
        }

        return switch (identifier) {
            case ROOT -> new Nodes.RootNode(nodeHash, properties, children);
            case MODEL -> new Nodes.ModelNode(nodeHash, properties, children);
            case MESH -> new Nodes.MeshNode(nodeHash, properties, children);
            case HAIR -> new Nodes.HairNode(nodeHash, properties, children);
            case BLEND_SHAPE -> new Nodes.BlendShapeNode(nodeHash, properties, children);
            case SKELETON -> new Nodes.SkeletonNode(nodeHash, properties, children);
            case BONE -> new Nodes.BoneNode(nodeHash, properties, children);
            case IK_HANDLE -> new Nodes.IkHandleNode(nodeHash, properties, children);
            case CONSTRAINT -> new Nodes.ConstraintNode(nodeHash, properties, children);
            case ANIMATION -> new Nodes.AnimationNode(nodeHash, properties, children);
            case CURVE -> new Nodes.CurveNode(nodeHash, properties, children);
            case CURVE_MODE_OVERRIDE -> new Nodes.CurveModeOverrideNode(nodeHash, properties, children);
            case NOTIFICATION_TRACK -> new Nodes.NotificationTrackNode(nodeHash, properties, children);
            case MATERIAL -> new Nodes.MaterialNode(nodeHash, properties, children);
            case FILE -> new Nodes.FileNode(nodeHash, properties, children);
            case COLOR -> new Nodes.ColorNode(nodeHash, properties, children);
            case INSTANCE -> new Nodes.InstanceNode(nodeHash, properties, children);
            case METADATA -> new Nodes.MetadataNode(nodeHash, properties, children);
        };
    }

    public final CastNodeID identifier() {
        return identifier;
    }

    public final long hash() {
        return hash;
    }

    public Map<String, CastProperty> properties() {
        return properties;
    }

    public List<CastNode> children() {
        return children;
    }

    public int length() {
        int result = 0x18;
        for (CastProperty property : properties.values()) {
            result += property.length();
        }
        for (CastNode child : children) {
            result += child.length();
        }
        return result;
    }

    public <T extends CastNode> Optional<T> getChildOfType(Class<T> type) {
        return children.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

    public <T extends CastNode> List<T> getChildrenOfType(Class<T> type) {
        return children.stream().filter(type::isInstance).map(type::cast).toList();
    }

    public <T> Optional<T> getProperty(String name, Function<Object, ? extends T> mapper) {
        return Optional.ofNullable(properties.get(name))
            .map(p -> mapper.apply(p.value()));
    }

    public <T extends CastNode> T createChild(T child) {
        children.add(child);
        return child;
    }

    public void createProperty(CastPropertyID identifier, String name, Object value) {
        properties.put(name, new CastProperty(identifier, name, value));
    }

    public void createIntProperty(String name, int value) {
        long l = Integer.toUnsignedLong(value);
        if (l <= 0xFF) {
            createProperty(CastPropertyID.BYTE, name, (byte) l);
        } else if (l <= 0xFFFF) {
            createProperty(CastPropertyID.SHORT, name, (short) l);
        } else {
            createProperty(CastPropertyID.INT, name, (int) l);
        }
    }

    public void createIntBufferProperty(String name, Buffer value) {
        Buffer buffer = Buffers.shrink(value);
        switch (buffer) {
            case ByteBuffer _ -> createProperty(CastPropertyID.BYTE, name, buffer);
            case ShortBuffer _ -> createProperty(CastPropertyID.SHORT, name, buffer);
            case IntBuffer _ -> createProperty(CastPropertyID.INT, name, buffer);
            default -> throw new IllegalArgumentException("Only integral buffers supported");
        }
    }

    boolean parseBoolean(Object value) {
        int i = ((Number) value).intValue();
        return switch (i) {
            case 0 -> true;
            case 1 -> false;
            default -> throw new IllegalArgumentException("Expected 0 or 1 but got " + i);
        };
    }
}
