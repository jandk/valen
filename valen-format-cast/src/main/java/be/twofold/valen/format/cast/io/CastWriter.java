package be.twofold.valen.format.cast.io;

import be.twofold.valen.format.cast.node.*;
import be.twofold.valen.format.cast.property.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.function.*;

public final class CastWriter implements Closeable {
    private final BinaryWriter writer;

    public CastWriter(OutputStream out) {
        this.writer = new BinaryWriter(out);
    }

    public void write(CastNode node) throws IOException {
//        var rootNode = new CastNode(CastNodeID.ROOT, hash.getAndIncrement())
//            .addChild(switch (node) {
//                case ModelNode model -> mapModel(model);
//                default -> throw new IllegalArgumentException("Unexpected value: " + node);
//            });

        writer.writeInt(0x74736163); // magic
        writer.writeInt(1); // version
        writer.writeInt(1); // rootNodeCount
        writer.writeInt(0); // flags
        // writeNode(rootNode);
    }

    private void writeNode(CastNode node) throws IOException {
        writer.writeInt(node.identifier().id());
        writer.writeInt(node.length());
        writer.writeLong(node.hash());
        writer.writeInt(node.properties().size());
        writer.writeInt(node.children().size());

        for (CastProperty property : node.properties().values()) {
            writeProperty(property);
        }

        for (CastNode child : node.children()) {
            writeNode(child);
        }
    }

    private void writeProperty(CastProperty property) throws IOException {
        var name = property.name().getBytes(StandardCharsets.UTF_8);
        writer.writeShort(property.identifier().id());
        writer.writeShort((short) name.length);
        writer.writeInt(property.arrayLength());
        writer.writeBytes(name);

        if (property.identifier() == CastPropertyID.STRING) {
            writer.writeBytes(property.asString().getBytes(StandardCharsets.UTF_8));
            writer.writeByte((byte) 0);
        } else {
            writer.writeBytes(toByteArray(property.asBuffer()));
        }
    }

    // TODO: Fix this shit
    private byte[] toByteArray(Buffer buffer) {
        buffer.rewind();
        return switch (buffer) {
            case ByteBuffer bb -> bb.array();
            case ShortBuffer sb -> allocateAndApply(buffer, bb -> bb.asShortBuffer().put(sb));
            case IntBuffer ib -> allocateAndApply(buffer, bb -> bb.asIntBuffer().put(ib));
            case LongBuffer lb -> allocateAndApply(buffer, bb -> bb.asLongBuffer().put(lb));
            case FloatBuffer fb -> allocateAndApply(buffer, bb -> bb.asFloatBuffer().put(fb));
            case DoubleBuffer db -> allocateAndApply(buffer, bb -> bb.asDoubleBuffer().put(db));
            case CharBuffer cb -> allocateAndApply(buffer, bb -> bb.asCharBuffer().put(cb));
        };
    }

    private byte[] allocateAndApply(Buffer buffer, Consumer<ByteBuffer> consumer) {
        var byteBuffer = ByteBuffer
            .allocate(byteSize(buffer))
            .order(ByteOrder.LITTLE_ENDIAN);
        consumer.accept(byteBuffer);
        return byteBuffer.array();
    }

    private int byteSize(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer bb -> bb.limit();
            case ShortBuffer sb -> sb.limit() * Short.BYTES;
            case IntBuffer ib -> ib.limit() * Integer.BYTES;
            case LongBuffer lb -> lb.limit() * Long.BYTES;
            case FloatBuffer fb -> fb.limit() * Float.BYTES;
            case DoubleBuffer db -> db.limit() * Double.BYTES;
            case CharBuffer cb -> cb.limit() * Character.BYTES;
        };
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
