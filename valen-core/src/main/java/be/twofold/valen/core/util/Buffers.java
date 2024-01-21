package be.twofold.valen.core.util;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static ByteBuffer toByteBuffer(Buffer buffer) {
        buffer.rewind();
        switch (buffer) {
            case ByteBuffer byteBuffer -> {
                return byteBuffer;
            }
            case ShortBuffer shortBuffer -> {
                var bb = allocate(shortBuffer.capacity() * Short.BYTES);
                bb.asShortBuffer().put(shortBuffer);
                return bb;
            }
            case IntBuffer intBuffer -> {
                var bb = allocate(intBuffer.capacity() * Integer.BYTES);
                bb.asIntBuffer().put(intBuffer);
                return bb;
            }
            case LongBuffer longBuffer -> {
                var bb = allocate(longBuffer.capacity() * Long.BYTES);
                bb.asLongBuffer().put(longBuffer);
                return bb;
            }
            case FloatBuffer floatBuffer -> {
                var bb = allocate(floatBuffer.capacity() * Float.BYTES);
                bb.asFloatBuffer().put(floatBuffer);
                return bb;
            }
            case DoubleBuffer doubleBuffer -> {
                var bb = allocate(doubleBuffer.capacity() * Double.BYTES);
                bb.asDoubleBuffer().put(doubleBuffer);
                return bb;
            }
            case CharBuffer charBuffer -> {
                var bb = allocate(charBuffer.capacity() * Character.BYTES);
                bb.asCharBuffer().put(charBuffer);
                return bb;
            }
        }
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

}
