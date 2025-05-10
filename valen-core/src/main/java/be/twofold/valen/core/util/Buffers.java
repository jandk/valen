package be.twofold.valen.core.util;

import org.slf4j.*;

import java.nio.*;

public final class Buffers {
    private static final Logger log = LoggerFactory.getLogger(Buffers.class);

    private Buffers() {
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer
            .allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static void copy(ByteBuffer src, ByteBuffer dst) {
        copy(src, dst, dst.remaining());
    }

    public static void copy(ByteBuffer src, ByteBuffer dst, int length) {
        dst.put(src.slice().limit(length));
        src.position(src.position() + length);
    }

    public static byte[] toArray(ByteBuffer buffer) {
        if (buffer.hasArray() &&
            buffer.position() == 0 &&
            buffer.limit() == buffer.capacity()
        ) {
            return buffer.array();
        }

        log.warn("Actually copying buffer of size {}", buffer.remaining());
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }


    public static Buffer shrink(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer _ -> buffer;
            case ShortBuffer sb -> shrinkShortBuffer(sb);
            case IntBuffer ib -> shrinkIntBuffer(ib);
            case LongBuffer lb -> shrinkLongBuffer(lb);
            default -> throw new IllegalArgumentException("Only integral buffers are supported");
        };
    }

    public static Buffer shrinkShortBuffer(ShortBuffer buffer) {
        var duplicate = buffer.duplicate();
        var max = unsignedMax(duplicate);

        if (max >= 0xFF) {
            return buffer;
        } else {
            return shortToByteBuffer(duplicate);
        }
    }

    public static Buffer shrinkIntBuffer(IntBuffer buffer) {
        var duplicate = buffer.duplicate();
        var max = unsignedMax(duplicate);

        if (max >= 0xFFFF) {
            return buffer;
        } else if (max >= 0xFF) {
            return intToShortBuffer(duplicate);
        } else {
            return intToByteBuffer(duplicate);
        }
    }

    public static Buffer shrinkLongBuffer(LongBuffer buffer) {
        var duplicate = buffer.duplicate();
        var max = unsignedMax(duplicate);

        if (max > 0xFFFFFFFFL) {
            return buffer;
        } else if (max >= 0xFFFF) {
            return longToIntBuffer(duplicate);
        } else if (max >= 0xFF) {
            return longToShortBuffer(duplicate);
        } else {
            return longToByteBuffer(duplicate);
        }
    }

    // region Conversion

    public static ByteBuffer shortToByteBuffer(ShortBuffer buffer) {
        buffer.rewind();
        var result = ByteBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((byte) buffer.get());
        }
        return result.flip();
    }

    public static ShortBuffer intToShortBuffer(IntBuffer buffer) {
        buffer.rewind();
        var result = ShortBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((short) buffer.get());
        }
        return result.flip();
    }

    public static ByteBuffer intToByteBuffer(IntBuffer buffer) {
        buffer.rewind();
        var result = ByteBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((byte) buffer.get());
        }
        return result.flip();
    }

    public static ByteBuffer longToByteBuffer(LongBuffer buffer) {
        buffer.rewind();
        var result = ByteBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((byte) buffer.get());
        }
        return result.flip();
    }

    public static ShortBuffer longToShortBuffer(LongBuffer buffer) {
        buffer.rewind();
        var result = ShortBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((short) buffer.get());
        }
        return result.flip();
    }

    public static IntBuffer longToIntBuffer(LongBuffer buffer) {
        buffer.rewind();
        var result = IntBuffer.allocate(buffer.capacity());
        while (buffer.hasRemaining()) {
            result.put((int) buffer.get());
        }
        return result.flip();
    }

    // endregion

    // region Unsigned Max

    public static short unsignedMax(ShortBuffer buffer) {
        buffer.rewind();
        short max = 0;
        while (buffer.hasRemaining()) {
            var value = (short) (buffer.get() ^ Short.MIN_VALUE);
            if (max < value) {
                max = value;
            }
        }
        return (short) (max ^ Short.MIN_VALUE);
    }

    public static int unsignedMax(IntBuffer buffer) {
        buffer.rewind();
        var max = 0;
        while (buffer.hasRemaining()) {
            var value = buffer.get() ^ Integer.MIN_VALUE;
            if (max < value) {
                max = value;
            }
        }
        return max ^ Integer.MIN_VALUE;
    }

    public static long unsignedMax(LongBuffer buffer) {
        buffer.rewind();
        long max = 0;
        while (buffer.hasRemaining()) {
            var value = buffer.get() ^ Long.MIN_VALUE;
            if (max < value) {
                max = value;
            }
        }
        return max ^ Long.MIN_VALUE;
    }

    // endregion

}
