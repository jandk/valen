package be.twofold.valen.format.cast;

import java.nio.*;
import java.util.function.*;

final class Buffers {
    private Buffers() {
    }

    static Buffer shrink(Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            return buffer;
        }

        if (buffer instanceof ShortBuffer shortBuffer) {
            ShortBuffer duplicate = shortBuffer.duplicate().rewind();

            int max = 0;
            while (duplicate.hasRemaining()) {
                max = Math.max(max, Short.toUnsignedInt(duplicate.get()));
            }

            duplicate.rewind();
            if (max <= 0xFF) {
                ByteBuffer result = ByteBuffer.allocate(duplicate.limit());
                while (duplicate.hasRemaining()) {
                    result.put((byte) duplicate.get());
                }
                return result.flip();
            }
            return shortBuffer;
        }

        if (buffer instanceof IntBuffer intBuffer) {
            IntBuffer duplicate = intBuffer.duplicate().rewind();

            long max = 0;
            while (duplicate.hasRemaining()) {
                max = Math.max(max, Integer.toUnsignedLong(duplicate.get()));
            }

            duplicate.rewind();
            if (max <= 0xFF) {
                ByteBuffer result = ByteBuffer.allocate(duplicate.limit());
                while (duplicate.hasRemaining()) {
                    result.put((byte) duplicate.get());
                }
                return result.flip();
            }
            if (max <= 0xFFFF) {
                ShortBuffer result = ShortBuffer.allocate(duplicate.limit());
                while (duplicate.hasRemaining()) {
                    result.put((short) duplicate.get());
                }
                return result.flip();
            }
            return intBuffer;
        }

        throw new IllegalArgumentException("Only integral buffers are supported");
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
        ByteBuffer byteBuffer = ByteBuffer
            .allocate(byteSize(buffer))
            .order(ByteOrder.LITTLE_ENDIAN);
        consumer.accept(byteBuffer);
        return byteBuffer.array();
    }

    private static int byteSize(Buffer buffer) {
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
}
