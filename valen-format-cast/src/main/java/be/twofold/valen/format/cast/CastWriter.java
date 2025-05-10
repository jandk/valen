package be.twofold.valen.format.cast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class CastWriter {
    private final BinaryWriter writer;

    private CastWriter(BinaryWriter writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    static void writer(Cast cast, OutputStream out) throws IOException {
        try (BinaryWriter writer = new BinaryWriter(new BufferedOutputStream(out))) {
            new CastWriter(writer).write(cast);
        }
    }

    private void write(Cast cast) throws IOException {
        writer.writeInt(0x74736163); // magic
        writer.writeInt(1); // version
        writer.writeInt(cast.rootNodes.size()); // rootNodeCount
        writer.writeInt(0); // flags

        for (CastNode rootNode : cast.rootNodes) {
            writeNode(rootNode);
        }
    }

    private void writeNode(CastNode node) throws IOException {
        writer.writeInt(node.identifier().id());
        writer.writeInt(node.length());
        writer.writeLong(node.hash());
        writer.writeInt(node.properties.size());
        writer.writeInt(node.children.size());

        for (CastProperty property : node.properties.values()) {
            writeProperty(property);
        }

        for (CastNode child : node.children) {
            writeNode(child);
        }
    }

    private void writeProperty(CastProperty property) throws IOException {
        byte[] rawName = property.name().getBytes(StandardCharsets.UTF_8);
        writer.writeShort(property.identifier().id());
        writer.writeShort((short) rawName.length);
        writer.writeInt(property.arrayLength());
        writer.writeBytes(rawName);

        if (property.arrayLength() == 1) {
            writeSingle(property.identifier(), property.value());
        } else {
            // Buffers.toByteArray does all the heavy lifting here
            writer.writeBytes(Buffers.toByteArray((Buffer) property.value()));
        }
    }

    private void writeSingle(CastPropertyID identifier, Object value) throws IOException {
        switch (identifier) {
            case BYTE -> writer.writeByte((byte) value);
            case SHORT -> writer.writeShort((short) value);
            case INT -> writer.writeInt((int) value);
            case LONG -> writer.writeLong((long) value);
            case FLOAT -> writer.writeFloat((float) value);
            case DOUBLE -> writer.writeDouble((double) value);
            case STRING -> {
                writer.writeBytes(value.toString().getBytes(StandardCharsets.UTF_8));
                writer.writeByte((byte) 0);
            }
            case VECTOR2 -> {
                Vec2 vec = (Vec2) value;
                writer.writeFloat(vec.x());
                writer.writeFloat(vec.y());
            }
            case VECTOR3 -> {
                Vec3 vec = (Vec3) value;
                writer.writeFloat(vec.x());
                writer.writeFloat(vec.y());
                writer.writeFloat(vec.z());
            }
            case VECTOR4 -> {
                Vec4 vec = (Vec4) value;
                writer.writeFloat(vec.x());
                writer.writeFloat(vec.y());
                writer.writeFloat(vec.z());
                writer.writeFloat(vec.w());
            }
        }
    }
}
