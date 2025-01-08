package be.twofold.valen.gltf;

import java.nio.*;
import java.util.function.*;

final class Buffers {
    static int byteSize(Buffer buffer) {
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

    static byte[] toByteArray(Buffer buffer) {
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

    private static byte[] allocateAndApply(Buffer buffer, Consumer<ByteBuffer> consumer) {
        var byteBuffer = ByteBuffer
            .allocateDirect(byteSize(buffer))
            .order(ByteOrder.LITTLE_ENDIAN);
        consumer.accept(byteBuffer);
        return byteBuffer.array();
    }
}
