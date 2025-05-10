package be.twofold.valen.format.cast.io;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static Buffer shrink(Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            return buffer;
        }

        if (buffer instanceof ShortBuffer shortBuffer) {
            var duplicate = shortBuffer.duplicate().rewind();

            short max = Short.MIN_VALUE;
            while (duplicate.hasRemaining()) {
                var value = (short) (duplicate.get() ^ Short.MIN_VALUE);
                max = (short) Math.max(max, value);
            }
            max ^= Short.MIN_VALUE;

            duplicate.rewind();
            if (max <= 0xFF) {
                var result = ByteBuffer.allocate(duplicate.capacity());
                while (duplicate.hasRemaining()) {
                    result.put((byte) duplicate.get());
                }
                return result.flip();
            }
            return shortBuffer;
        }

        if (buffer instanceof IntBuffer intBuffer) {
            var duplicate = intBuffer.duplicate().rewind();

            var max = Integer.MIN_VALUE;
            while (duplicate.hasRemaining()) {
                var value = duplicate.get() ^ Integer.MIN_VALUE;
                max = Math.max(max, value);
            }
            max ^= Integer.MIN_VALUE;

            duplicate.rewind();
            if (max <= 0xFF) {
                var result = ByteBuffer.allocate(duplicate.capacity());
                while (duplicate.hasRemaining()) {
                    result.put((byte) duplicate.get());
                }
                return result.flip();
            }
            if (max <= 0xFFFF) {
                var result = ShortBuffer.allocate(duplicate.capacity());
                while (duplicate.hasRemaining()) {
                    result.put((short) duplicate.get());
                }
                return result.flip();
            }
            return intBuffer;
        }

        throw new IllegalArgumentException("Only integral buffers are supported");
    }

}
